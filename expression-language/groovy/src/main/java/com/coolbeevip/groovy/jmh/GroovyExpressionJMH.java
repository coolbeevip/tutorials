package com.coolbeevip.groovy.jmh;

import com.coolbeevip.groovy.Evaluator;
import com.coolbeevip.groovy.GroovyScriptEvaluator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.Throughput)
@Fork(value = 1, warmups = 2)
@Threads(4)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GroovyExpressionJMH {

  @Benchmark
  public void groovyConcatString(BenchmarkState state) {
    String result = state.evaluator.evaluate("greeter", "join", state.params);
  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    Map<String, Object> params = new HashMap<>();
    Evaluator<String> evaluator = new GroovyScriptEvaluator();

    @Setup(Level.Trial)
    public void initialize() throws InstantiationException, IllegalAccessException {
      params.put("first", "Lei");
      params.put("last", "Zhang");
      params.put("delimiter", " ");
      String groovyScript = "class Greeter {\n" +
          "  String join(Map<String, Object> arguments) {\n" +
          "    arguments.get(\"first\") + arguments.get(\"delimiter\") + arguments.get(\"last\")\n" +
          "  }\n" +
          "}";
      evaluator.loadGroovyScript("greeter", groovyScript);
    }

    @TearDown
    public void tearDown() {
      params.clear();
    }
  }
}