package com.coolbeevip.flink.wordcount;

import com.beust.jcommander.JCommander;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class WindowWordCount {

  public static void main(String[] args) throws Exception {

    WindowWordCountArgs windowWordCountArgs = new WindowWordCountArgs();
    JCommander.newBuilder()
        .addObject(windowWordCountArgs)
        .build()
        .parse(args);

    EnvironmentPolicy environmentPolicy = new EnvironmentPolicy(windowWordCountArgs.environment);
    StreamExecutionEnvironment env = environmentPolicy.getEnv();

    DataStream<Tuple2<String, Integer>> dataStream = env
        .socketTextStream(windowWordCountArgs.host, windowWordCountArgs.port)
        .flatMap(new Splitter())
        .keyBy(value -> value.f0)
        .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
        .sum(1);

    dataStream.print();

    JobExecutionResult result = env.execute("Window WordCount");
    System.out.println(result);
  }

  public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
    @Override
    public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
      for (String word : sentence.split(" ")) {
        out.collect(new Tuple2<String, Integer>(word, 1));
      }
    }
  }

}