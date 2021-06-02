package com.coolbeevip.rocksdb;

import static com.coolbeevip.rocksdb.schema.MySchemaImpl.HOT_MESSAGE;
import static org.hamcrest.MatcherAssert.assertThat;

import com.coolbeevip.rocksdb.schema.RecordRow;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.Test;

public class RocksDbVariableTest {

  @Test
  public void test() {
    RecordRow record = RecordRow.builder()
        .uuid(UUID.randomUUID().toString())
        .build();
    assertThat(HOT_MESSAGE.getKey(record.getUuid()),
        Matchers.is(HOT_MESSAGE.getKeySerializer().serialize(record.getUuid())));
    assertThat(HOT_MESSAGE.getValue(record),
        Matchers.is(HOT_MESSAGE.getValueSerializer().serialize(record)));
  }
}