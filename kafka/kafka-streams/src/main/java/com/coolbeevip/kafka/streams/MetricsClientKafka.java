package com.coolbeevip.kafka.streams;

import com.coolbeevip.faker.core.MetricsClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

@Slf4j
public class MetricsClientKafka implements MetricsClient {

  private final KafkaProducer producer;
  private final String topic;

  public MetricsClientKafka(KafkaProducer producer, String topic) {
    this.producer = producer;
    this.topic = topic;
  }

  @Override
  public void push(String json) {
    ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);
    producer.send(record, (md, ex) -> {
      if (ex != null) {
        log.error("exception occurred in producer for review :{}, exception is {}",
            record.value(),
            ex);
      } else {
        log.debug("Sent {} to {} with offset {} at {}", json, md.partition(), md.offset(),
            md.timestamp());
      }
    });
  }
}