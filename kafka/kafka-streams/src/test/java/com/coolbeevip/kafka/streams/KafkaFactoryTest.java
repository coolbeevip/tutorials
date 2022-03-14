package com.coolbeevip.kafka.streams;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.awaitility.Awaitility;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class KafkaFactoryTest {

  static KafkaContainer kafka;
  String inputTopic = "input-topic";
  String outputTopic = "outputTopic";

  @BeforeClass
  public static void setup() {
    /**
     Confluentinc Kafka:6.2.2 and Apache Kafka 2.8.1 Compatibility
     */
    kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.2"));
    kafka.start();
  }

  @AfterClass
  public static void tearDown() {
    kafka.stop();
  }

  @Test
  @SneakyThrows
  public void wordCountTopologyTest() {

    Map<String, Long> actualWordCounts = new HashMap();

    // 创建 Kafka 连接
    String bootstrapServers = kafka.getBootstrapServers();
    String applicationId = "tutorials-kafka-streams";
    String stateDirectory = "tutorials-kafka-streams-dir";
    KafkaFactory factory = new KafkaFactory(bootstrapServers, applicationId,
        stateDirectory);

    // 启动流拓扑
    factory.startJob(buildWordCountTopology(actualWordCounts));

    // 发送测试数据
    KafkaProducer producer = factory.getKafkaProducer();
    String[] messages = new String[]{"this is a pony", "this is a horse and pony"};
    Arrays.stream(messages).forEach(m -> {
      ProducerRecord<String, String> record = new ProducerRecord<>(inputTopic, m);
      producer.send(record, (md, ex) -> {
        if (ex != null) {
          log.error("exception occurred in producer for review :{}, exception is {}",
              record.value(),
              ex);
        } else {
          log.info("Sent msg to {} with offset {} at {}", md.partition(), md.offset(),
              md.timestamp());
        }
      });
    });

    Map<String, Long> expectWordCounts = new HashMap<>();
    expectWordCounts.put("this", 2l);
    expectWordCounts.put("is", 2l);
    expectWordCounts.put("a", 2l);
    expectWordCounts.put("horse", 1l);
    expectWordCounts.put("and", 1l);
    expectWordCounts.put("pony", 2l);

    Awaitility.await().atMost(60, SECONDS).until(
        () -> actualWordCounts.equals(expectWordCounts));
  }


  /**
   * 单词数量统计
   */
  public Topology buildWordCountTopology(Map<String, Long> actualWordCounts) {
    StreamsBuilder builder = new StreamsBuilder();
    KStream<String, String> textLines = builder.stream(inputTopic);
    Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
    KTable<String, Long> wordCounts = textLines
        .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
        .groupBy((key, word) -> word)
        .count();
    wordCounts.toStream().foreach((word, count) -> actualWordCounts.put(word, count));
    return builder.build();
  }
}