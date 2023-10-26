package com.coolbeevip.redis.lettuce;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.protocol.ProtocolVersion;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

  //@Test
  public void connectTestWithProtocolVersion() throws UnsupportedEncodingException {
    String password = URLEncoder.encode("fs-sat-ctgcache-01#SSew1_DHwe23r", "UTF-8");
    ClientOptions options = ClientOptions.builder()
        .protocolVersion(ProtocolVersion.RESP2).build();
    RedisURI redisURI = RedisURI.create("redis://"+password+"@10.34.24.65:31046/20");
    RedisClient redisClient = RedisClient.create(redisURI);
    redisClient.setOptions(options);
    StatefulRedisConnection<String, String> connection = redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();
    String infoString = syncCommands.info();
    System.out.println(infoString);
  }
}
