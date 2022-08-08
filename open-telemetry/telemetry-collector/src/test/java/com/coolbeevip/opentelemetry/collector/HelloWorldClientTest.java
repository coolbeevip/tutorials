package com.coolbeevip.opentelemetry.collector;

import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class HelloWorldClientTest {
  /**
   * This rule manages automatic graceful shutdown for the registered servers and channels at the
   * end of test.
   */
  @Rule
  public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private final GreeterGrpc.GreeterImplBase serviceImpl =
      Mockito.mock(GreeterGrpc.GreeterImplBase.class, AdditionalAnswers.delegatesTo(
          new GreeterGrpc.GreeterImplBase() {
            // By default the client will receive Status.UNIMPLEMENTED for all RPCs.
            // You might need to implement necessary behaviors for your test here, like this:
            //
            // @Override
            // public void sayHello(HelloRequest request, StreamObserver<HelloReply> respObserver) {
            //   respObserver.onNext(HelloReply.getDefaultInstance());
            //   respObserver.onCompleted();
            // }
          }));

  private HelloWorldClient client;

  @Before
  public void setUp() throws Exception {
    // Generate a unique in-process server name.
    String serverName = InProcessServerBuilder.generateName();

    // Create a server, add service, start, and register for automatic graceful shutdown.
    grpcCleanup.register(InProcessServerBuilder
        .forName(serverName).directExecutor().addService(serviceImpl).build().start());

    // Create a client channel and register for automatic graceful shutdown.
    ManagedChannel channel = grpcCleanup.register(
        InProcessChannelBuilder.forName(serverName).directExecutor().build());

    // Create a HelloWorldClient using the in-process channel;
    client = new HelloWorldClient(channel);
  }

  /**
   * To test the client, call from the client against the fake server, and verify behaviors or state
   * changes from the server side.
   */
  @Test
  public void greet_messageDeliveredToServer() {
    ArgumentCaptor<HelloRequest> requestCaptor = ArgumentCaptor.forClass(HelloRequest.class);

    client.greet("test name");

    Mockito.verify(serviceImpl)
        .sayHello(requestCaptor.capture(), ArgumentMatchers.<StreamObserver<HelloReply>>any());
    Assert.assertEquals("test name", requestCaptor.getValue().getName());
  }
}