package com.coolbeevip.kafka.streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

public class KafkaConfiguration {

  private final Properties streamsConfiguration;
  private final String inputTopic;
  private final Path stateDirectory;


  public KafkaConfiguration(String bootstrapServers, String inputTopic, String appId,
      String stateDirectory)
      throws IOException {
    this.inputTopic = inputTopic;
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
  }

  public void buildTopology(boolean cleanUp) {
    StreamsBuilder builder = new StreamsBuilder();

    KStream<String, String> textLines = builder.stream(this.inputTopic);
    Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
    KTable<String, Long> wordCounts = textLines
        .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
        .groupBy((key, word) -> word)
        .count();
    wordCounts.toStream()
        .foreach((word, count) -> System.out.println("word: " + word + " -> " + count));
    String outputTopic = "outputTopic";
    wordCounts.toStream()
        .to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));

    Topology topology = builder.build();

    KafkaStreams streams = new KafkaStreams(topology, this.streamsConfiguration);
    if (cleanUp) {
      streams.cleanUp();
    }
    streams.start();
  }
}