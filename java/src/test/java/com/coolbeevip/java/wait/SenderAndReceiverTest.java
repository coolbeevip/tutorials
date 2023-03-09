package com.coolbeevip.java.wait;

import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class SenderAndReceiverTest {

  @Test
  public void test() throws InterruptedException {
    Data data = new Data();
    Sender sender = new Sender(data);
    Receiver receiver = new Receiver(data);

    new Thread(sender).start();
    new Thread(receiver).start();

    await().atMost(10, SECONDS).until(() -> receiver.getMessages().size() == 4);
  }
}
