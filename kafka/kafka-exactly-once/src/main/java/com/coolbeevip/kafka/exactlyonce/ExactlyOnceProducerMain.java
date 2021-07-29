package com.coolbeevip.kafka.exactlyonce;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExactlyOnceProducerMain {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) {

    ExactlyOnceAdmin admin = new ExactlyOnceAdmin(ExactlyOnceConstant.bootstrapServers);
    try {
      admin.createTopic(ExactlyOnceConstant.topic, 2, (short) 1);
    } catch (Exception e) {
      log.error("create topic fails", e);
    }

    ExactlyOnceProducer exactlyOnceProducer = new ExactlyOnceProducer(
        ExactlyOnceConstant.bootstrapServers,
        ExactlyOnceConstant.transactionId);
    List<ProducerRecord<String, String>> records = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      records
          .add(new ProducerRecord<>(ExactlyOnceConstant.topic, UUID.randomUUID().toString(),
              UUID.randomUUID().toString()));
    }
    exactlyOnceProducer.send(records);
  }
}