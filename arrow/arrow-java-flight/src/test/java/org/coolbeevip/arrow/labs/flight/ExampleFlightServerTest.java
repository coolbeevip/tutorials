package org.coolbeevip.arrow.labs.flight;

import java.io.IOException;
import java.util.List;
import org.apache.arrow.flight.AsyncPutListener;
import org.apache.arrow.flight.FlightClient;
import org.apache.arrow.flight.FlightClient.ClientStreamListener;
import org.apache.arrow.flight.FlightDescriptor;
import org.apache.arrow.flight.FlightInfo;
import org.apache.arrow.flight.FlightStream;
import org.apache.arrow.flight.Location;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.util.AutoCloseables;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.types.Types;
import org.apache.arrow.vector.types.pojo.Schema;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExampleFlightServerTest {

  static {
    // compatible JDK 11
    System.setProperty("io.netty.tryReflectionSetAccessible", "true");
  }

  private BufferAllocator allocator;
  private BufferAllocator caseAllocator;
  private ExampleFlightServer server;
  private FlightClient client;

  @Before
  public void start() throws IOException {
    allocator = new RootAllocator(Long.MAX_VALUE);
    Location l = Location.forGrpcInsecure("localhost", 12233);
    server = new ExampleFlightServer(allocator, l);
    server.start();
    client = FlightClient.builder(allocator, l).build();
    caseAllocator = allocator.newChildAllocator("test-case", 0, Long.MAX_VALUE);
  }

  @After
  public void after() throws Exception {
    // 关闭连接
    AutoCloseables.close(server, client, caseAllocator, allocator);
  }

  @Test
  public void streamPutAndGetTest() {
    BufferAllocator a = caseAllocator;
    final int size = 10;

    IntVector iv = new IntVector("c1", a);

    VectorSchemaRoot root = VectorSchemaRoot.of(iv);
    ClientStreamListener listener = client.startPut(FlightDescriptor.path("hello"), root,
        new AsyncPutListener());

    //batch 1
    root.allocateNew();
    for (int i = 0; i < size; i++) {
      iv.set(i, i);
    }
    iv.setValueCount(size);
    root.setRowCount(size);
    listener.putNext();

    // batch 2

    root.allocateNew();
    for (int i = 0; i < size; i++) {
      iv.set(i, i + size);
    }
    iv.setValueCount(size);
    root.setRowCount(size);
    listener.putNext();
    root.clear();
    listener.completed();

    // wait for ack to avoid memory leaks.
    listener.getResult();

    FlightInfo info = client.getInfo(FlightDescriptor.path("hello"));
    try (final FlightStream stream = client.getStream(info.getEndpoints().get(0).getTicket())) {
      VectorSchemaRoot newRoot = stream.getRoot();
      Schema schema = newRoot.getSchema();
      System.out.println("schema is " + schema.toString());
      while (stream.next()) {
        List<FieldVector> fieldVector = newRoot.getFieldVectors();
        System.out
            .println("number of fieldVectors (corresponding to columns) : " + fieldVector.size());
        for (int j = 0; j < fieldVector.size(); j++) {
          Types.MinorType mt = fieldVector.get(j).getMinorType();
          switch (mt) {
            case INT:
              showIntAccessor(fieldVector.get(j));
              break;
            default:
              throw new Exception(" MinorType " + mt);
          }
        }
        newRoot.clear();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void showIntAccessor(FieldVector fx) {
    IntVector intVector = ((IntVector) fx);
    for (int j = 0; j < intVector.getValueCount(); j++) {
      if (!intVector.isNull(j)) {
        int value = intVector.get(j);
        System.out.println("\t\t intAccessor[" + j + "] " + value);
      } else {
        System.out.println("\t\t intAccessor[" + j + "] : NULL ");
      }
    }
  }
}