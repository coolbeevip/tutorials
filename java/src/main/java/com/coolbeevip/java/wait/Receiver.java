package com.coolbeevip.java.wait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Receiver implements Runnable {
  private List<String> messages = new ArrayList<>();
  private final Data data;

  public Receiver(Data data) {
    this.data = data;
  }


  public void run() {
    for (String receivedMessage = data.receive();
         !"End".equals(receivedMessage);
         receivedMessage = data.receive()) {

      System.out.println(receivedMessage);
      messages.add(receivedMessage);

      //Thread.sleep() to mimic heavy server-side processing
      try {
        Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.err.println("Thread Interrupted");
      }
    }
  }

  public List<String> getMessages() {
    return Collections.unmodifiableList(this.messages);
  }
}