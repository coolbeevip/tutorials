package com.coolbeevip.flink.wordcount;

import com.beust.jcommander.JCommander;
import com.coolbeevip.flink.function.SplitterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SocketTextStreamFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class WindowWordCount {

  private final Integer tumbling;
  private final SourceFunction<String> sourceFunction;
  private final FlatMapFunction<String, Tuple2<String, Integer>> splitterFunction;

  public WindowWordCount(SourceFunction<String> sourceFunction, Integer tumbling, FlatMapFunction<String, Tuple2<String, Integer>> splitterFunction) {
    this.sourceFunction = sourceFunction;
    this.tumbling = tumbling;
    this.splitterFunction = splitterFunction;
  }

  void build(StreamExecutionEnvironment see) {
    DataStream<Tuple2<String, Integer>> dataStream = see
        .addSource(sourceFunction, TypeInformation.of(String.class)).setParallelism(1)
        .flatMap(this.splitterFunction)
        .keyBy(value -> value.f0)
        .window(TumblingProcessingTimeWindows.of(Time.seconds(this.tumbling)))
        .sum(1);

    dataStream.print();
  }

  public static void main(String[] args) throws Exception {

    WindowWordCountArgs windowWordCountArgs = new WindowWordCountArgs();
    JCommander.newBuilder()
        .addObject(windowWordCountArgs)
        .build()
        .parse(args);

    EnvironmentPolicy environmentPolicy = new EnvironmentPolicy(windowWordCountArgs.environment);

    new WindowWordCount(
        new SocketTextStreamFunction(windowWordCountArgs.host, windowWordCountArgs.port, "\n", 0L),
        windowWordCountArgs.tumbling,
        new SplitterFunction()).build(environmentPolicy.getSee());

    environmentPolicy.getSee().execute("Window WordCount");
  }
}