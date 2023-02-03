package com.coolbeevip.flink.wordcount;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.streaming.api.functions.source.datagen.DataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class WordGenerator implements DataGenerator<String> {
  private final static Logger log = LoggerFactory.getLogger(WordGenerator.class);
  private final List<Object> words;
  private AtomicInteger index = new AtomicInteger();

  public WordGenerator(List<Object> words) {
    this.words = words;
  }

  @Override
  public boolean hasNext() {
    return index.get() < this.words.size();
  }

  @Override
  public String next() {
    String s;
    if (this.words.get(index.get()) instanceof Long) {
      try {
        TimeUnit.SECONDS.sleep(((Long) this.words.get(index.get())).longValue());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      s = "WAIT";
    } else {
      s = this.words.get(index.get()).toString();
    }
    log.info("SEND {} {}/{} from {}", s, index, this.words.size(), this);
    index.incrementAndGet();
    return s+"\n";
  }

  @Override
  public void open(String s, FunctionInitializationContext functionInitializationContext, RuntimeContext runtimeContext) throws Exception {

  }
}
