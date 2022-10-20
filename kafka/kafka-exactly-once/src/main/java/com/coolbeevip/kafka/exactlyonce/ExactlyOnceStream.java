package com.coolbeevip.kafka.exactlyonce;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ExactlyOnceStream {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final KafkaStreams streams;
  private final String bootstrapServers;
  private final String topic;
  private final String applicationId;

  public ExactlyOnceStream(String bootstrapServers, String topic, String applicationId) {
    this.bootstrapServers = bootstrapServers;
    this.topic = topic;
    this.applicationId = applicationId;
    this.streams = createKafkaStreams();
  }

  public synchronized void start() {
    streams.cleanUp();
    streams.start();
  }

  private KafkaStreams createKafkaStreams() {
    // streams configuration
    Properties streamsConfiguration = new Properties();
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
    streamsConfiguration.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG,
        StreamsConfig.EXACTLY_ONCE); // Exactly Once
    streamsConfiguration.put(
        StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
        Serdes.String().getClass().getName());
    streamsConfiguration.put(
        StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
        Serdes.String().getClass().getName());
    streamsConfiguration.put(
        StreamsConfig.STATE_DIR_CONFIG,
        tempDirectory(null, null).getAbsolutePath());

    // build topology
    StreamsBuilder builder = new StreamsBuilder();
    KStream<String, String> kafkaStream =
        builder.stream(this.topic, Consumed.with(Serdes.String(), Serdes.String()));

    kafkaStream.foreach((k, v) -> businessLogic(k, v));

    Topology topology = builder.build();

    // build kafka streams
    return new KafkaStreams(topology, streamsConfiguration);
  }

  private File tempDirectory(final Path parent, String prefix) {
    final File file;
    prefix = prefix == null ? "kafka-" : prefix;
    try {
      file = parent == null ?
          Files.createTempDirectory(prefix).toFile()
          : Files.createTempDirectory(parent, prefix).toFile();
    } catch (final IOException ex) {
      throw new RuntimeException("Failed to create a temp dir", ex);
    }
    file.deleteOnExit();
    return file;
  }

  private void businessLogic(String k, String v) {
    log.info("value {}", v);
  }
}