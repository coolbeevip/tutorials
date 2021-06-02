package com.coolbeevip.rocksdb;

import static com.coolbeevip.rocksdb.schema.MessageSchema.HOT_MESSAGE;
import static org.hamcrest.MatcherAssert.assertThat;

import com.coolbeevip.rocksdb.core.RocksDbAccessor;
import com.coolbeevip.rocksdb.core.RocksDbFactory;
import com.coolbeevip.rocksdb.schema.Message;
import com.coolbeevip.rocksdb.schema.MessageSchema;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author zhanglei
 */

public class RocksDbTest {

  static RocksDbAccessor dbAccessor;

  @BeforeClass
  public static void setup() throws Exception {
    Path databaseDir = Paths.get("rocksdb-data");
    RocksDbConfiguration configuration = RocksDbConfiguration.hotDefaults(databaseDir);
    dbAccessor = RocksDbFactory
        .create(configuration, MessageSchema.INSTANCE.getAllColumns());
  }

  @AfterClass
  public static void tearDown() throws Exception {
    dbAccessor.close();
  }

  @Before
  public void beforeEach() {
    dbAccessor.stream(HOT_MESSAGE).forEach(a -> {
      dbAccessor.delete(HOT_MESSAGE, a.getKey());
    });
  }

  @Test
  public void curdTest() throws Exception {
    Message record = Message.builder()
        .uuid(UUID.randomUUID().toString())
        .build();

    // put
    dbAccessor.put(HOT_MESSAGE, record.getUuid(), record);

    // get
    Optional<Message> optionalRecordRow = dbAccessor.get(HOT_MESSAGE, record.getUuid());
    assertThat(optionalRecordRow.isPresent(), Matchers.is(true));
    assertThat(record.getUuid(), Matchers.is(optionalRecordRow.get().getUuid()));

    // delete
    dbAccessor.delete(HOT_MESSAGE, record.getUuid());
    assertThat(dbAccessor.get(HOT_MESSAGE, record.getUuid()).isPresent(), Matchers.is(false));
  }

  @Test
  public void batchPutTest() {
    Map<String,Message> data = new HashMap<>();
    for (int i = 0; i < 100; i++) {
      Message message = Message.builder()
          .uuid(String.valueOf(i))
          .build();
      data.put(message.getUuid(),message);
    }
    dbAccessor.put(HOT_MESSAGE,data);
    assertThat(dbAccessor.getAll(HOT_MESSAGE).size(), Matchers.is(100));
  }

  @Test
  public void getAllTest() {
    for (int i = 0; i < 100; i++) {
      Message record = Message.builder()
          .uuid(String.valueOf(i))
          .build();
      dbAccessor.put(HOT_MESSAGE, record.getUuid(), record);
    }
    assertThat(dbAccessor.getAll(HOT_MESSAGE).size(), Matchers.is(100));
  }
}