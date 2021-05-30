package org.coolbeevip.grpc.labs.demo;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.coolbeevip.grpc.labs.demo.grpc.ExchangeServiceGrpc;
import org.coolbeevip.grpc.labs.demo.grpc.ExchangeServiceGrpc.ExchangeServiceBlockingStub;
import org.coolbeevip.grpc.labs.demo.grpc.ExchangeServiceGrpc.ExchangeServiceStub;
import org.coolbeevip.grpc.labs.demo.grpc.RequestMessage;
import org.coolbeevip.grpc.labs.demo.grpc.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangeClient {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final ManagedChannel channel;
  private final ExchangeServiceBlockingStub blockingStub;
  private final ExchangeServiceStub asyncStub;
  private AtomicInteger i = new AtomicInteger(0);

  public ExchangeClient(String host, int port) {
    channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext(true).build();
    blockingStub = ExchangeServiceGrpc.newBlockingStub(channel);
    asyncStub = ExchangeServiceGrpc.newStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  //阻塞调用
  public ResponseMessage sendMessage(String text) {
    RequestMessage request = RequestMessage.newBuilder().setId(i.getAndIncrement()).setText(text)
        .build();
    return blockingStub.sendMessage(request);
  }

  //服务端流
  public Iterator<ResponseMessage> sendServerStream(String text) {
    RequestMessage request = RequestMessage.newBuilder().setId(i.getAndIncrement()).setText(text)
        .build();
    return blockingStub.sendServerStream(request);
  }

  //客户端流数据
  public void sendClientStream(StreamObserver<ResponseMessage> responseStreamObserver, String text) {
    StreamObserver<RequestMessage> requestStreamObserver = asyncStub
        .sendClientStream(responseStreamObserver);
    requestStreamObserver
        .onNext(RequestMessage.newBuilder().setId(i.getAndIncrement()).setText(text).build());
    requestStreamObserver.onCompleted();
  }

  //双向流数据
  public StreamObserver<RequestMessage> sendBidirectionStream(StreamObserver<ResponseMessage> responseStreamObserver) {
    StreamObserver<RequestMessage> requestStreamObserver = asyncStub
        .sendBidirectionStream(responseStreamObserver);
    return requestStreamObserver;
  }
}