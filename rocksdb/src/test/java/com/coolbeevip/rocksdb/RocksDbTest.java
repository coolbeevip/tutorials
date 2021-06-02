package com.coolbeevip.rocksdb;

import static com.coolbeevip.rocksdb.schema.MySchemaImpl.HOT_MESSAGE;
import static org.hamcrest.MatcherAssert.assertThat;

import com.coolbeevip.rocksdb.core.RocksDbAccessor;
import com.coolbeevip.rocksdb.core.RocksDbInstanceFactory;
import com.coolbeevip.rocksdb.schema.MySchemaImpl;
import com.coolbeevip.rocksdb.schema.RecordRow;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RocksDbTest {

  static RocksDbAccessor dbAccessor;

  @BeforeClass
  public static void setup() throws Exception {
    Path databaseDir = Paths.get("rocksdb-data");
    RocksDbConfiguration configuration = RocksDbConfiguration.hotDefaults(databaseDir);
    dbAccessor = RocksDbInstanceFactory
        .create(configuration, MySchemaImpl.INSTANCE.getAllColumns());
  }

  @AfterClass
  public static void tearDown() throws Exception {
    dbAccessor.close();
  }

  @Test
  public void curdTest() throws Exception {
    RecordRow record = RecordRow.builder()
        .uuid(UUID.randomUUID().toString())
        .build();

    // put
    dbAccessor.put(HOT_MESSAGE, record.getUuid(), record);

    // get
    Optional<RecordRow> optionalRecordRow = dbAccessor.get(HOT_MESSAGE, record.getUuid());
    assertThat(optionalRecordRow.isPresent(), Matchers.is(true));
    assertThat(record.getUuid(), Matchers.is(optionalRecordRow.get().getUuid()));

    // delete
    dbAccessor.delete(HOT_MESSAGE, record.getUuid());
    assertThat(dbAccessor.get(HOT_MESSAGE, record.getUuid()).isPresent(), Matchers.is(false));
  }
}