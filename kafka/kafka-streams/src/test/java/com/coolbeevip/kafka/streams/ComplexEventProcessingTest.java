package com.coolbeevip.kafka.streams;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.coolbeevip.faker.Faker;
import com.coolbeevip.faker.Host;
import com.coolbeevip.faker.Proc;
import com.coolbeevip.faker.core.Constants.RiskLevel;
import com.coolbeevip.faker.core.MetricsClient;
import com.coolbeevip.faker.playground.Play;
import com.coolbeevip.faker.playground.WeightedCollection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.awaitility.Awaitility;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class ComplexEventProcessingTest {

  ObjectMapper mapper = new ObjectMapper();
  static KafkaContainer kafka;
  static KafkaFactory factory;
  String inputTopic = "input-topic";

  @BeforeClass
  public static void setup() throws IOException {
    /**
     Confluentinc Kafka:6.2.2 and Apache Kafka 2.8.1 Compatibility
     */
    kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.2"));
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
   * 单事件 CPU 利用率阀值过滤
   */
  @Test
  @SneakyThrows
  public void singleEventCpuUsageThresholdTopologyTest() {
    // 启动流拓扑
    double threshold = 0.8;
    Set<String> filterSet = new HashSet<>();
    factory.startJob(buildSingleEventCpuUsageThresholdTopology(filterSet, threshold));

    // 启动性能数据模拟
    WeightedCollection<RiskLevel> weightedCollection = new WeightedCollection();
    weightedCollection.add(1, RiskLevel.LOW);
    weightedCollection.add(2, RiskLevel.MID);
    weightedCollection.add(3, RiskLevel.NORMAL);
    weightedCollection.add(5, RiskLevel.HIGH);
    playStart(factory.getKafkaProducer(), weightedCollection);

    Awaitility.await().atMost(60, SECONDS).until(
        () -> filterSet.size() > 0 && filterSet.stream().filter(s -> {
          try {
            return mapper.readTree(s).at("/cpu/usage").asDouble() > threshold;
          } catch (JsonProcessingException e) {
            return false;
          }
        }).count() == filterSet.size());

    filterSet.forEach(s -> log.info(s));
  }

  /**
   * 单事件 CPU和内存利用率阀值过滤
   */
  @Test
  @SneakyThrows
  public void singleEventMultiThresholdTopologyTest() {
    // 启动流拓扑
    double threshold = 0.8;
    Set<String> filterSet = new HashSet<>();
    factory.startJob(buildSingleEventMultiThresholdTopology(filterSet, threshold));

    // 启动性能数据模拟
    WeightedCollection<RiskLevel> weightedCollection = new WeightedCollection();
    weightedCollection.add(1, RiskLevel.LOW);
    weightedCollection.add(2, RiskLevel.MID);
    weightedCollection.add(3, RiskLevel.NORMAL);
    weightedCollection.add(5, RiskLevel.HIGH);
    playStart(factory.getKafkaProducer(), weightedCollection);

    Awaitility.await().atMost(60, SECONDS).until(
        () -> filterSet.size() > 0 && filterSet.stream().filter(s -> {
          try {
            return mapper.readTree(s).at("/cpu/usage").asDouble() > threshold &
                mapper.readTree(s).at("/memory/free").asDouble() / mapper.readTree(s)
                    .at("/memory/total").asDouble() > threshold;
          } catch (JsonProcessingException e) {
            return false;
          }
        }).count() == filterSet.size());

    filterSet.forEach(s -> log.info(s));
  }


  /**
   * 单事件-单指标阀值过滤(cpu利用率大于阀值)
   */
  private Topology buildSingleEventCpuUsageThresholdTopology(Set<String> filterSet,
      double threshold) {
    StreamsBuilder builder = new StreamsBuilder();
    KStream<String, String> textLines = builder.stream(inputTopic);
    textLines.filter((k, v) -> {
      try {
        JsonNode root = mapper.readTree(v);
        return root.at("/cpu/usage").asDouble() > threshold;
      } catch (JsonProcessingException e) {
        log.error("解析原始数据错误 {}", v);
        return false;
      }
    }).foreach((k, v) -> {
      filterSet.add(v);
    });
    return builder.build();
  }

  /**
   * 单事件-多指标标阀值过滤(cpu利用率和内存利用率都大于阀值)
   */
  private Topology buildSingleEventMultiThresholdTopology(Set<String> filterSet, double threshold) {
    StreamsBuilder builder = new StreamsBuilder();
    KStream<String, String> textLines = builder.stream(inputTopic);
    textLines.filter((k, v) -> {
      try {
        JsonNode root = mapper.readTree(v);
        return root.at("/cpu/usage").asDouble() > threshold &
            root.at("/memory/free").asDouble() / root.at("/memory/total").asDouble() > threshold;
      } catch (JsonProcessingException e) {
        log.error("解析原始数据错误 {}", v);
        return false;
      }
    }).foreach((k, v) -> {
      filterSet.add(v);
    });
    return builder.build();
  }

  /**
   * 模拟产生数据
   */
  private void playStart(KafkaProducer producer, WeightedCollection<RiskLevel> weightedCollection)
      throws ExecutionException, InterruptedException {
    MetricsClient client = new MetricsClientKafka(producer, inputTopic);
    Play play = new Play(client, weightedCollection);
    Faker faker = new Faker();

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
}