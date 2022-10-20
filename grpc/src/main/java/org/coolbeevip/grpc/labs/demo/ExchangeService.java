package org.coolbeevip.grpc.labs.demo;

import io.grpc.stub.StreamObserver;
import org.coolbeevip.grpc.labs.demo.grpc.ExchangeServiceGrpc.ExchangeServiceImplBase;
import org.coolbeevip.grpc.labs.demo.grpc.RequestMessage;
import org.coolbeevip.grpc.labs.demo.grpc.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhanglei
 */
public class ExchangeService extends ExchangeServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private AtomicInteger id = new AtomicInteger(0);

  @Override
  public void sendMessage(RequestMessage request,
      StreamObserver<ResponseMessage> responseObserver) {
    ResponseMessage response = ResponseMessage.newBuilder()
        .setId(id.incrementAndGet())
        .setFromId(request.getId())
        .setText("Hi! client, I received your message [" + request.getText() + "]").build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void sendServerStream(RequestMessage request,
      StreamObserver<ResponseMessage> responseObserver) {
    ResponseMessage response = ResponseMessage.newBuilder()
        .setId(id.incrementAndGet())
        .setFromId(request.getId())
        .setText("Hi! client, I received your message [" + request.getText() + "]").build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<RequestMessage> sendClientStream(
      final StreamObserver<ResponseMessage> responseObserver) {
    return new StreamObserver<RequestMessage>() {
      List<RequestMessage> messages = new ArrayList();

      @Override
      public void onNext(RequestMessage requestMessage) {
        //客户端每次写入都会触发
        messages.add(requestMessage);
      }

      @Override
      public void onError(Throwable throwable) {
        LOG.error("", throwable);
      }

      @Override
      public void onCompleted() {
        //客户端写入完毕时触发
        for (RequestMessage message : messages) {
          ResponseMessage response = ResponseMessage.newBuilder()
              .setId(id.incrementAndGet())
              .setFromId(message.getId())
              .setText("Hi! client, I received your message [" + message.getText() + "]").build();
          responseObserver.onNext(response);
          responseObserver.onCompleted();
        }
      }
    };
  }

  @Override
  public StreamObserver<RequestMessage> sendBidirectionStream(
      final StreamObserver<ResponseMessage> responseObserver) {
    return new StreamObserver<RequestMessage>() {

      @Override
      public void onNext(RequestMessage requestMessage) {
        int counter = 0;
        String text = requestMessage.getText();
        if ("end".equals(text)) {
          LOG.info(String.format("Client say: %s", text));
          responseObserver.onCompleted();
        } else {
          if ("begin".equals(text)) {
            LOG.info(String.format("Client say: %s", text));
          } else {
            counter = Integer.parseInt(requestMessage.getText());
            LOG.info(String.format("Client say: %s", counter));
          }
          ResponseMessage response = ResponseMessage.newBuilder()
              .setId(id.incrementAndGet())
              .setText(String.valueOf(++counter)).build();
          responseObserver.onNext(response);
          LOG.info(String.format("Server say: %s", counter));
        }
      }

      @Override
      public void onError(Throwable throwable) {
        LOG.error("", throwable);
      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();
      }
    };
  }
}