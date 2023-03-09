package com.coolbeevip.tutorials.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JsonTest {

  @Test
  public void beanToMap() {
    TestBean bean = new TestBean("tom", 20);
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> map = objectMapper.convertValue(bean, Map.class);
    assertThat(bean.getName(), is(map.get("name")));
    assertThat(bean.getAge(), is(map.get("age")));
  }

  @Test
  public void jsonStringToMap() throws JsonProcessingException {
    String json = "{\"name\":\"rack\",\"age\":20}";
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> map = objectMapper.readValue(json, Map.class);
    assertThat("rack", is(map.get("name")));
    assertThat(20, is(map.get("age")));
  }
}
