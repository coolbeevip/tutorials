package com.coolbeevip.rocksdb.core;

import static com.coolbeevip.rocksdb.schema.MessageSchema.HOT_MESSAGE;
import static org.hamcrest.MatcherAssert.assertThat;

import com.coolbeevip.rocksdb.schema.Message;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * @author zhanglei
 */

public class RocksDbColumnFamilyTest {

  @Test
  public void test() {
    Message record = Message.builder()
        .uuid(UUID.randomUUID().toString())
        .build();
    assertThat(HOT_MESSAGE.getKey(record.getUuid()),
        Matchers.is(HOT_MESSAGE.getKeySerializer().serialize(record.getUuid())));
    assertThat(HOT_MESSAGE.getValue(record),
        Matchers.is(HOT_MESSAGE.getValueSerializer().serialize(record)));
  }
}