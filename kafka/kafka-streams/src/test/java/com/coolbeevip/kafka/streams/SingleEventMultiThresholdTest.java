package com.coolbeevip.kafka.streams;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.coolbeevip.faker.core.Constants.RiskLevel;
import com.coolbeevip.faker.playground.WeightedCollection;
import com.coolbeevip.kafka.streams.base.KafkaStreamsTestKit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashSet;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.awaitility.Awaitility;
import org.junit.Test;

@Slf4j
public class SingleEventMultiThresholdTest extends KafkaStreamsTestKit {

  String inputTopic = "input-topic";

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
    playStart(inputTopic, factory.getKafkaProducer(), weightedCollection);

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
}