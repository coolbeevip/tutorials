package com.coolbeevip.redis.lettuce;

import io.lettuce.core.RedisURI;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class LettuceTest {

  // https://github.com/lettuce-io/lettuce-core/issues/2254
  @Test
  public void sentinelUriTest() {
    RedisURI redisURI = RedisURI.create("redis-sentinel://abc%40%23123@h1:6397,h2:6397,h3:6397/5?sentinelMasterId=masterId");
    assertThat(redisURI.getSentinelMasterId(), Matchers.is("masterId"));
    assertThat(redisURI.getSentinels().get(0).getHost(), Matchers.is("h1"));
    assertThat(String.valueOf(redisURI.getPassword()), Matchers.is("abc@#123"));
  }
}
