package org.coolbeevip.arrow.labs.flight;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import org.apache.arrow.flight.FlightServer;
import org.apache.arrow.flight.Location;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.util.AutoCloseables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhanglei
 * */
public class ExampleFlightServer implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final FlightServer flightServer;
  private final Location location;
  private final BufferAllocator allocator;
  private final InMemoryStore mem;

  public ExampleFlightServer(BufferAllocator allocator, Location location) {
    this.allocator = allocator.newChildAllocator("flight-server", 0, Long.MAX_VALUE);
    this.location = location;
    this.mem = new InMemoryStore(this.allocator, location);
    this.flightServer = FlightServer.builder(allocator, location, mem).build();
    LOG.info(String.format("%s Started!!!", location));
  }

  public Location getLocation() {
    return location;
  }

  public void start() throws IOException {
    flightServer.start();
  }

  public void awaitTermination() throws InterruptedException {
    flightServer.awaitTermination();
  }

  public InMemoryStore getStore() {
    return mem;
  }

  @Override
  public void close() throws Exception {
    AutoCloseables.close(mem, flightServer, allocator);
  }

}