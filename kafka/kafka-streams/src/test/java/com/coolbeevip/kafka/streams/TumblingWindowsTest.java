package com.coolbeevip.kafka.streams;

import com.coolbeevip.faker.Host;
import com.coolbeevip.faker.core.Constants.RiskLevel;
import com.coolbeevip.faker.playground.Play;
import com.coolbeevip.faker.playground.WeightedCollection;
import com.coolbeevip.kafka.streams.base.KafkaStreamsTestKit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.awaitility.Awaitility;
import org.junit.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.LongStream;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 翻滚窗口统计每个主机，每5秒的事件数量
 * 输入: 每隔1秒发送一个消息，累计发送 60 个消息
 * 输出: 每 10 秒计汇总一次消息数量
 */
@Slf4j
public class TumblingWindowsTest extends KafkaStreamsTestKit {

  String inputTopic = "input-topic";
  static long MESSAGE_TOTAL = 60;

  @Test
  @SneakyThrows
  public void counterMessageCountByTumblingWindowsTest() {

    Map<String, Long> actualCounts = new HashMap<>();

    // 启动流拓扑
    factory.startJob(buildTumblingWindowsTopology(actualCounts));

    // 发送测试数据
    Host host = faker.host("192.168.0.1", 16, 976490576, 2033396);
    WeightedCollection<RiskLevel> weightedCollection = new WeightedCollection();
    weightedCollection.add(25, RiskLevel.LOW);
    weightedCollection.add(25, RiskLevel.MID);
    weightedCollection.add(25, RiskLevel.NORMAL);
    weightedCollection.add(25, RiskLevel.HIGH);
    Play play = getPlay(inputTopic, factory.getKafkaProducer());
    LongStream.range(0, MESSAGE_TOTAL).forEach(s -> {
      try {
        play.push(host.take(weightedCollection.next()).id().type().timestamp().version(1).toJSON());
        SECONDS.sleep(1);
      } catch (Exception e) {
        log.error("", e);
      }
    });

    Awaitility.await().atMost(60, SECONDS)
        .until(() -> actualCounts.values().stream().reduce(0l, (a, b) -> a + b).equals(MESSAGE_TOTAL));

    actualCounts.forEach((k, v) -> log.info("{}={}", k, v));
  }

  private Topology buildTumblingWindowsTopology(Map<String, Long> actualCounts) {
    StreamsBuilder builder = new StreamsBuilder();
    final KStream<String, String> stream = builder.stream(inputTopic);
    KTable<Windowed<String>, Long> eventCounts = stream
        .map((k, v) -> {
          try {
            JsonNode json = mapper.readTree(v);
            return new KeyValue<>(json.get("id").asText(), v);
          } catch (JsonProcessingException e) {
            log.error("JSON 解析错误: {}", v, e);
            return null;
          }
        })
        .filter((k, v) -> v != null)
        .groupByKey()
        .windowedBy(TimeWindows.of(Duration.ofSeconds(10))).count();
    eventCounts.toStream().foreach((k, v) -> {
      actualCounts.put(k.toString(), v);
    });
    return builder.build();
  }
}