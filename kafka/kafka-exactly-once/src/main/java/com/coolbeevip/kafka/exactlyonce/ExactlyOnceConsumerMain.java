package com.coolbeevip.kafka.exactlyonce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CountDownLatch;

public class ExactlyOnceConsumerMain {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws InterruptedException {

    CountDownLatch mainLatch = new CountDownLatch(1);

    ExactlyOnceAdmin admin = new ExactlyOnceAdmin(ExactlyOnceConstant.bootstrapServers);
    try {
      admin.createTopic(ExactlyOnceConstant.topic, 2, (short) 1);
    } catch (Exception e) {
      log.error("create topic fails", e);
    }

    ExactlyOnceConsumer exactlyOnceConsumer = new ExactlyOnceConsumer(
        ExactlyOnceConstant.bootstrapServers,
        ExactlyOnceConstant.groupId,
        ExactlyOnceConstant.topic, false);
    exactlyOnceConsumer.start();

    mainLatch.await();
  }
}