package com.coolbeevip.kafka.exactlyonce;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExactlyOnceAdmin {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Properties properties;

  public ExactlyOnceAdmin(String bootstrapServers) {
    this.properties = new Properties();
    this.properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

  }

  /**
   * 要想全局有序，numPartitions 只能为 1
   * 要想高可用，replicationFactor 必须大于 0
   */
  public void createTopic(String topicName, Integer numPartitions, Short replicationFactor)
      throws ExecutionException, InterruptedException {
    try (Admin admin = Admin.create(properties)) {
      if (!admin.listTopics().names().get().stream().filter(name -> name.equals(topicName))
          .findAny().isPresent()) {
        NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
        admin.createTopics(Collections.singleton(newTopic)).values().get(topicName).get();
        log.warn("Topic {} numPartitions {} replicationFactor {} created", topicName, numPartitions,
            replicationFactor);
      } else {
        log.warn("Topic {} exist", topicName);
      }
    }
  }
}