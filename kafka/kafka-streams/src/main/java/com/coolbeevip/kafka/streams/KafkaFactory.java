package com.coolbeevip.kafka.streams;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class KafkaFactory {

  private final String bootstrapServers;
  private final Properties streamsConfiguration;
  private final Path stateDirectory;

  public KafkaFactory(String bootstrapServers, String appId,
      String stateDirectory)
      throws IOException {
    this(bootstrapServers, appId, stateDirectory, null);
  }

  public KafkaFactory(String bootstrapServers, String appId,
      String stateDirectory, Properties overrideStreamsConfiguration)
      throws IOException {
    this.bootstrapServers = bootstrapServers;
    this.streamsConfiguration = new Properties();
    streamsConfiguration.put(
        StreamsConfig.APPLICATION_ID_CONFIG, appId);
    streamsConfiguration.put(
        StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
        bootstrapServers);
    streamsConfiguration.put(
        StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
        Serdes.String().getClass().getName());
    streamsConfiguration.put(
        StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
        Serdes.String().getClass().getName());
    this.stateDirectory = Files.createTempDirectory(stateDirectory);
    streamsConfiguration.put(
        StreamsConfig.STATE_DIR_CONFIG, this.stateDirectory.toAbsolutePath().toString());

    // override default configuration
    if (overrideStreamsConfiguration != null) {
      overrideStreamsConfiguration.forEach((k, v) -> streamsConfiguration.put(k, v));
    }
  }

  public KafkaProducer getKafkaProducer() {
    Properties properties = new Properties();
    properties.put("bootstrap.servers", this.bootstrapServers);
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.put("retries", 3);
    properties.put("max.request.size", 1024 * 1024); // limit request size to 1MB
    return new KafkaProducer<>(properties);
  }

  public KafkaStreams startJob(Topology topology) {
    KafkaStreams streams = new KafkaStreams(topology, this.streamsConfiguration);
    streams.start();
    return streams;
  }
}