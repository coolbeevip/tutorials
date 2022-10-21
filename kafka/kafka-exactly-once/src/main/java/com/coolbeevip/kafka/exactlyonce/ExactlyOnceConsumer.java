package com.coolbeevip.kafka.exactlyonce;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singleton;
import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ISOLATION_LEVEL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

public class ExactlyOnceConsumer {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final KafkaConsumer<String, String> consumer;
  private final String bootstrapServers;
  private final String topic;
  private final String groupId;
  private final boolean seekToBeginning;

  private boolean started;
  public static List<String> receivedMessage = new ArrayList<>();

  public ExactlyOnceConsumer(String bootstrapServers, String groupId,
                             String topic, boolean seekToBeginning) {
    this.bootstrapServers = bootstrapServers;
    this.groupId = groupId;
    this.topic = topic;
    this.seekToBeginning = seekToBeginning;
    this.consumer = createKafkaConsumer();
  }

  public synchronized void start() {
    if (!started) {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.submit(() -> {
        while (true) {
          try {
            ConsumerRecords<String, String> records = consumer.poll(ofSeconds(60));
            if (!records.isEmpty()) {
              records.records(this.topic)
                  .forEach(r -> {
                    receivedMessage.add(r.value());
                    log.info("partition {} offset {} value {}", r.partition(), r.offset(),
                        r.value());
                  });
              consumer.commitSync();
              for (TopicPartition partition : records.partitions()) {
                List<ConsumerRecord<String, String>> partitionedRecords = records
                    .records(partition);
                long offset = partitionedRecords.get(partitionedRecords.size() - 1).offset();
                log.info("commit partition {} offset {}", partition, offset + 1);
                // TODO 此处最好记录 offset，以便在重启或者再均衡时 seek 到对应位置
              }
            }
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
        }
      });
      started = true;
    }
  }

  private KafkaConsumer<String, String> createKafkaConsumer() {
    Properties props = new Properties();
    props.put(BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
    props.put(GROUP_ID_CONFIG, this.groupId);
    props.put(ENABLE_AUTO_COMMIT_CONFIG, "false"); // 必须禁用自动提交
    props.put(ISOLATION_LEVEL_CONFIG, "read_committed"); // 事务性数据的消费策略，只读取已提交的数据
    props.put(KEY_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(VALUE_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(singleton(this.topic), new ConsumerRebalanceListener() {

      @Override
      public void onPartitionsRevoked(
          Collection<TopicPartition> collection) {

      }

      @Override
      public void onPartitionsAssigned(
          Collection<TopicPartition> collection) {
        //重新分配分区之后和消费者开始消费之前被调用
        if (seekToBeginning) {
          consumer.seekToBeginning(collection);
        }
        // TODO 再均衡时考虑 seek 到上次的 offset
      }
    });
    return consumer;
  }
}