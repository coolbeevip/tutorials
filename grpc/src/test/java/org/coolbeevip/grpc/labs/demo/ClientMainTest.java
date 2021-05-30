package org.coolbeevip.grpc.labs.demo;

import io.grpc.stub.StreamObserver;
import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.coolbeevip.grpc.labs.demo.grpc.RequestMessage;
import org.coolbeevip.grpc.labs.demo.grpc.ResponseMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMainTest {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private AtomicInteger i = new AtomicInteger(0);
  ExchangeClient client;
  StreamObserver<RequestMessage> requestStreamObserver;
  int counter = 1;

  @Before
  public void setup() {
    client = new ExchangeClient("0.0.0.0", 8081);
  }

  @After
  public void clean() throws InterruptedException {
    client.shutdown();
  }

  @Test
  public void sendMessageTest() {
    System.out.println("阻塞调用");
    ResponseMessage response = client.sendMessage("Hi! I'm client");
    System.out.println(String
        .format("Server response id=%d text=%s ack=%d", response.getId(), response.getText(),
            response.getFromId()));
  }

  @Test
  public void sendServerStreamTest() {
    System.out.println("服务端流调用");
    Iterator<ResponseMessage> responses = client.sendServerStream("Hi!, I'm client");
    while (responses.hasNext()) {
      ResponseMessage response = responses.next();
      System.out.println(String
          .format("Server response id=%d text=%s ack=%d", response.getId(), response.getText(),
              response.getFromId()));
    }
  }

  @Test
  public void sendClientStreamTest() throws InterruptedException {
    System.out.println("客户端流调用");
    final CountDownLatch latch = new CountDownLatch(2);
    //创建一个应答者接收返回的数据
    StreamObserver<ResponseMessage> responseStreamObserver = new StreamObserver<ResponseMessage>() {
      public void onNext(ResponseMessage response) {
        System.out.println(String
            .format("Server response id=%d text=%s ack=%d", response.getId(), response.getText(),
                response.getFromId()));
      }

      public void onError(Throwable throwable) {
        throwable.printStackTrace();
        latch.countDown();
      }

      public void onCompleted() {
        latch.countDown();
      }
    };
    client.sendClientStream(responseStreamObserver, "Hi, I'm client");
    client.sendClientStream(responseStreamObserver, "Hi, It's me again");
    //等待一分钟
    if (!latch.await(1, TimeUnit.MINUTES)) {
      System.out.println("方法在1分钟内没有完成");
    }
  }

  /**
   * 由客户端发起交替报数，当客户端报数大于10后则停止报数
   */
  @Test
  public void sendBidirectionStreamTest() throws InterruptedException {
    LOG.info("双向流调用,交替报数");
    final CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<ResponseMessage> responseStreamObserver = new StreamObserver<ResponseMessage>() {
      public void onNext(ResponseMessage response) {
        String text = response.getText();
        LOG.info(String.format("Server say: %s", text));
        counter = Integer.parseInt(text) + 1;
        if (counter < 10) {
          LOG.info(String.format("Client say: %s", counter));
          requestStreamObserver.onNext(RequestMessage.newBuilder().setId(i.getAndIncrement())
              .setText(String.valueOf(counter)).build());
        } else {
          LOG.info(String.format("Client say: %s", counter));
          LOG.info(String.format("Client say: %s", "end"));
          requestStreamObserver.onNext(RequestMessage.newBuilder().setId(i.getAndIncrement())
              .setText("end").build());
        }
      }

      public void onError(Throwable throwable) {
        throwable.printStackTrace();
        latch.countDown();
      }

      public void onCompleted() {
        latch.countDown();
      }
    };

    requestStreamObserver = client.sendBidirectionStream(responseStreamObserver);
    requestStreamObserver
        .onNext(RequestMessage.newBuilder().setId(i.getAndIncrement()).setText("begin").build());
    LOG.info("Client say: begin");
    if (!latch.await(1, TimeUnit.MINUTES)) {
      LOG.error("方法在1分钟内没有完成");
    }
  }

}