package com.coolbeevip.kafka.streams;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class KafkaConfigurationTest {

  static KafkaContainer kafka;
  String username = "admin";
  String password = "admin-secret";

  @BeforeClass
  public static void setup() {
    // Confluentinc Kafka:6.2.2 and Apache Kafka 2.8.1 Compatibility
    kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.2"));
    kafka.start();
  }

  @AfterClass
  public static void tearDown() {
    kafka.stop();
  }

  @Test
  @SneakyThrows
  public void test() {
    String bootstrapServers = kafka.getBootstrapServers();
    String inputTopic = "input-topic";
    String appId = "tutorials-"+ UUID.randomUUID();
    String stateDirectory = "tutorials-kafka-streams-dir";
    KafkaConfiguration configuration = new KafkaConfiguration(bootstrapServers, inputTopic, appId,
        stateDirectory);
    configuration.buildTopology(true);
    KafkaProducer producer = getKafkaProducer();
    String[] messages = new String[]{"this is a pony", "this is a horse and pony"};
    Arrays.stream(messages).forEach(m -> {
      ProducerRecord<String, String> record = new ProducerRecord<>(inputTopic, m);
      producer.send(record, (md, ex) -> {
        if (ex != null) {
          log.error("exception occurred in producer for review :{}, exception is {}", record.value(),
              ex);
        } else {
          log.debug("Sent msg to {} with offset {} at {}", md.partition(), md.offset(),
              md.timestamp());
        }
      });
    });
    Thread.sleep(500000);
  }

  private KafkaProducer getKafkaProducer() {
    Properties properties = new Properties();
    properties.put("bootstrap.servers", kafka.getBootstrapServers());
    //properties.put("sasl.mechanism", "PLAIN");
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.put("retries", 3);
    properties.put("max.request.size", 1024 * 1024); // limit request size to 1MB
    return new KafkaProducer<>(properties);
  }
}