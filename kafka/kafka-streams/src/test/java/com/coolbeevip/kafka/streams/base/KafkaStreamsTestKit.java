package com.coolbeevip.kafka.streams.base;

import com.coolbeevip.faker.Faker;
import com.coolbeevip.faker.Host;
import com.coolbeevip.faker.Proc;
import com.coolbeevip.faker.core.Constants.RiskLevel;
import com.coolbeevip.faker.core.MetricsClient;
import com.coolbeevip.faker.playground.Play;
import com.coolbeevip.faker.playground.WeightedCollection;
import com.coolbeevip.kafka.streams.KafkaFactory;
import com.coolbeevip.kafka.streams.MetricsClientKafka;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class KafkaStreamsTestKit {
  protected static Faker faker = new Faker();
  protected static KafkaContainer kafka;
  protected static KafkaFactory factory;
  protected static ObjectMapper mapper = new ObjectMapper();

  @BeforeClass
  public static void setup() throws IOException {
    /**
     Confluentinc Kafka:6.2.2 and Apache Kafka 2.8.1 Compatibility
     */
    kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.1.0-1-ubi8"));
    kafka.start();

    // 创建 Kafka 连接
    String bootstrapServers = kafka.getBootstrapServers();
    String applicationId = "tutorials-kafka-streams";
    String stateDirectory = "tutorials-kafka-streams-dir";
    factory = new KafkaFactory(bootstrapServers, applicationId, stateDirectory);
  }

  @AfterClass
  public static void tearDown() {
    kafka.stop();
  }


  /**
   * 模拟产生数据
   */
  protected void playStart(String inputTopic, KafkaProducer producer, WeightedCollection<RiskLevel> weightedCollection)
      throws ExecutionException, InterruptedException {
    MetricsClient client = new MetricsClientKafka(producer, inputTopic);
    Play play = new Play(client, weightedCollection);

    Host host1 = faker.host("192.168.0.1", 16, 976490576, 2033396);
    play.addHost(host1);
    Proc processWeb = faker.process("app-web", 65535, new BigInteger("8589934592"), host1);
    play.addProcess(processWeb);

    Host host2 = faker.host("192.168.0.1", 16, 976490576, 2033396);
    play.addHost(host2);
    Proc processBackend = faker.process("app-backend", 65535, new BigInteger("8589934592"),
        host2);
    play.addProcess(processBackend);

    play.go(TimeUnit.SECONDS, 1, TimeUnit.MINUTES, 10);
  }

  protected Play getPlay(String inputTopic, KafkaProducer producer){
    MetricsClient client = new MetricsClientKafka(producer, inputTopic);
    Play play = new Play(client);
    return play;
  }
}