package com.coolbeevip.groovy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class GroovyScriptEvaluatorTest {

  private Evaluator<String> evaluator = new GroovyScriptEvaluator();

  @Test
  @SneakyThrows
  public void test() {
    String groovyScript = FileUtils.readFileToString(Paths.get("src/test/resources/Greeter.groovy").toFile(), StandardCharsets.UTF_8);
    evaluator.loadGroovyScript("greeter", groovyScript);

    Map<String, Object> arguments = new HashMap<>();
    arguments.put("first", "Lei");
    arguments.put("last", "Zhang");
    arguments.put("delimiter", " ");
    String value = evaluator.evaluate("greeter", "join", arguments);
    assertThat(value, Matchers.is("Lei Zhang"));
  }

  @Test
  @SneakyThrows
  public void testTime() {
    String groovyScript = FileUtils.readFileToString(Paths.get("src/test/resources/Greeter.groovy").toFile(), StandardCharsets.UTF_8);
    evaluator.loadGroovyScript("greeter", groovyScript);

    Map<String, Object> arguments = new HashMap<>();
    arguments.put("first", "Lei");
    arguments.put("last", "Zhang");
    arguments.put("delimiter", " ");
    long total = 10000000;
    long begin = System.currentTimeMillis();
    for (int i = 0; i < total; i++) {
      String value = evaluator.evaluate("greeter", "join", arguments);
    }
    long end = System.currentTimeMillis();
    log.info("time {} ms, ops {}/ms", end - begin, total / (end - begin));
  }
}
