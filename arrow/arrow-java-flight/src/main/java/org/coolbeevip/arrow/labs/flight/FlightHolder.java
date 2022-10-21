package org.coolbeevip.arrow.labs.flight;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.arrow.flight.FlightDescriptor;
import org.apache.arrow.flight.FlightEndpoint;
import org.apache.arrow.flight.FlightInfo;
import org.apache.arrow.flight.Location;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.util.AutoCloseables;
import org.apache.arrow.util.Preconditions;
import org.apache.arrow.vector.dictionary.DictionaryProvider;
import org.apache.arrow.vector.types.pojo.Schema;
import org.apache.arrow.vector.util.DictionaryUtility;

/**
 * A logical collection of streams sharing the same schema.
 */
public class FlightHolder implements AutoCloseable {

  private final BufferAllocator allocator;
  private final FlightDescriptor descriptor;
  private final Schema schema;
  private final List<Stream> streams = new CopyOnWriteArrayList<>();
  private final DictionaryProvider dictionaryProvider;

  /**
   * Creates a new instance.
   *
   * @param allocator          The allocator to use for allocating buffers to store data.
   * @param descriptor         The descriptor for the streams.
   * @param schema             The schema for the stream.
   * @param dictionaryProvider The dictionary provider for the stream.
   */
  public FlightHolder(BufferAllocator allocator, FlightDescriptor descriptor, Schema schema,
                      DictionaryProvider dictionaryProvider) {
    Preconditions.checkArgument(!descriptor.isCommand());
    this.allocator = allocator.newChildAllocator(descriptor.toString(), 0, Long.MAX_VALUE);
    this.descriptor = descriptor;
    this.schema = schema;
    this.dictionaryProvider = dictionaryProvider;
  }

  /**
   * Returns the stream based on the ordinal of ExampleTicket.
   */
  public Stream getStream(ExampleTicket ticket) {
    Preconditions.checkArgument(ticket.getOrdinal() < streams.size(), "Unknown stream.");
    Stream stream = streams.get(ticket.getOrdinal());
    stream.verify(ticket);
    return stream;
  }

  /**
   * Adds a new streams which clients can populate via the returned object.
   */
  public Stream.StreamCreator addStream(Schema schema) {
    Preconditions.checkArgument(this.schema.equals(schema),
        "Stream schema inconsistent with existing schema.");
    return new Stream.StreamCreator(schema, dictionaryProvider, allocator, t -> {
      synchronized (streams) {
        streams.add(t);
      }
    });
  }

  /**
   * List all available streams as being available at <code>l</code>.
   */
  public FlightInfo getFlightInfo(final Location l) {
    final long bytes = allocator.getAllocatedMemory();
    final long records = streams.stream().collect(Collectors.summingLong(t -> t.getRecordCount()));

    final List<FlightEndpoint> endpoints = new ArrayList<>();
    int i = 0;
    for (Stream s : streams) {
      endpoints.add(
          new FlightEndpoint(
              new ExampleTicket(descriptor.getPath(), i, s.getUuid())
                  .toTicket(),
              l));
      i++;
    }
    return new FlightInfo(schema, descriptor, endpoints, bytes, records);
  }

  @Override
  public void close() throws Exception {
    // Close dictionaries
    final Set<Long> dictionaryIds = new HashSet<>();
    schema.getFields().forEach(
        field -> DictionaryUtility.toMessageFormat(field, dictionaryProvider, dictionaryIds));

    final Iterable<AutoCloseable> dictionaries = dictionaryIds.stream()
        .map(id -> (AutoCloseable) dictionaryProvider.lookup(id).getVector())::iterator;

    AutoCloseables.close(Iterables.concat(streams, ImmutableList.of(allocator), dictionaries));
  }
}