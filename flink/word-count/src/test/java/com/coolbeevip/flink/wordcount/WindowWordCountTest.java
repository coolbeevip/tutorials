package com.coolbeevip.flink.wordcount;

import com.coolbeevip.flink.function.SplitterFunction;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.datagen.DataGeneratorSource;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Arrays;

public class WindowWordCountTest {
  private static StreamExecutionEnvironment see;

  @ClassRule
  public static MiniClusterWithClientResource flinkCluster =
      new MiniClusterWithClientResource(
          new MiniClusterResourceConfiguration.Builder()
              .setNumberSlotsPerTaskManager(4)
              .setNumberTaskManagers(4)
              .build());

  @BeforeClass
  public static void setUp() {
    see = StreamExecutionEnvironment.getExecutionEnvironment();
    see.setParallelism(1);
  }

  @Test
  public void test() throws Exception {
    DataGeneratorSource<String> source = new DataGeneratorSource(new WordGenerator(Arrays.asList("A", "A", "B", "B", "B")), 1, 5l);
    new WindowWordCount(source, 5, new SplitterFunction()).build(see);
    see.execute("Window WordCount Test");
  }

}
