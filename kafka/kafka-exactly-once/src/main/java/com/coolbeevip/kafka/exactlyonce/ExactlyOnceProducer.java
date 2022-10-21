package com.coolbeevip.kafka.exactlyonce;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Properties;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.TRANSACTIONAL_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

public class ExactlyOnceProducer {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final String transactionId;
  private final String bootstrapServers;
  private final KafkaProducer<String, String> producer;

  public ExactlyOnceProducer(String bootstrapServers, String transactionId) {
    this.transactionId = transactionId;
    this.bootstrapServers = bootstrapServers;
    this.producer = createKafkaProducer();
  }

  /**
   * 发送时使用事务
   */
  public void send(List<ProducerRecord<String, String>> records) {
    producer.initTransactions();
    try {
      producer.beginTransaction();
      records.forEach(s -> {
        producer.send(s);
      });
      producer.commitTransaction();
    } catch (ProducerFencedException e) {
      log.error(e.getMessage(), e);
      producer.close();
    } catch (KafkaException e) {
      log.error(e.getMessage(), e);
      producer.abortTransaction();
    }
  }

  private KafkaProducer<String, String> createKafkaProducer() {

    Properties props = new Properties();
    props.put(BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
    props.put(ENABLE_IDEMPOTENCE_CONFIG, "true"); // 单分区幂等
    props.put(ACKS_CONFIG, "all");
    props.put(TRANSACTIONAL_ID_CONFIG, this.transactionId); // 跨分区事务ID，进程唯一，重启后不变
    props
        .put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    props.put(VALUE_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer");

    return new KafkaProducer(props);
  }
}