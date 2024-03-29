package com.coolbeevip.expression.spel.jmh;

import com.coolbeevip.expression.Evaluator;
import com.coolbeevip.expression.spel.SpELExpressionEvaluator;
import com.coolbeevip.expression.spel.custom.MyExpression;
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
public class SpELExpressionJMH {
  @Benchmark
  public void spelSubstring(BenchmarkState state) {
    String result = state.evaluator.evaluate("#full_name.substring(0, 6)", state.params);
  }

  @Benchmark
  public void javaSubstring(BenchmarkState state) {
    String result = state.params.get("full_name").toString().substring(0, 6);
  }

  @Benchmark
  public void spelConcatString(BenchmarkState state) {
    String result = state.evaluator.evaluate("#first_name + ' ' + #last_name", state.params);
  }

  @Benchmark
  public void javaConcatString(BenchmarkState state) {
    String result = state.params.get("first_name").toString() + " " + state.params.get("last_name").toString();
  }

  @Benchmark
  public void spelIfElse(BenchmarkState state) {
    String result = state.evaluator.evaluate("#full_name != null ? #full_name : #last_name != null ? #last_name : #first_name", state.params);
  }

  @Benchmark
  public void javaIfElse(BenchmarkState state) {
    String result = state.params.get("first_name") != null ? state.params.get("first_name").toString() : state.params.get("last_name") != null ? state.params.get("last_name").toString() : state.params.get("first_name").toString();
  }

  @Benchmark
  public void spelClass(BenchmarkState state) {
    String result = state.evaluator.evaluate("T(com.coolbeevip.expression.spel.custom.MyExpression).staticGender(#gender)", state.params);
  }

  @Benchmark
  public void javaClass(BenchmarkState state) {
    String result = MyExpression.staticGender(Integer.parseInt(state.params.get("gender").toString()));
  }

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    Map<String, Object> params = new HashMap<>();
    Evaluator<String> evaluator = new SpELExpressionEvaluator();

    @Setup(Level.Trial)
    public void initialize() {
      params.put("full_name", "Thomas Zhang");
      params.put("first_name", "Thomas");
      params.put("last_name", "Zhang");
      params.put("age", 30);
      params.put("gender", 1);
    }

    @TearDown
    public void tearDown() {
      params.clear();
    }
  }
}