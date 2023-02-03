package com.coolbeevip.flink.function;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplitterFunction implements FlatMapFunction<String, Tuple2<String, Integer>> {
  private final static Logger log = LoggerFactory.getLogger(SplitterFunction.class);
  private final static String DEFAULT_REGEX = " ";

  private final String regex;

  public SplitterFunction() {
    this.regex = DEFAULT_REGEX;
  }

  public SplitterFunction(String regex) {
    this.regex = regex;
  }

  @Override
  public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
    for (String word : sentence.split(this.regex)) {
      out.collect(new Tuple2<>(word, 1));
    }
    log.debug("RECEIVED {}", sentence);
  }
}
