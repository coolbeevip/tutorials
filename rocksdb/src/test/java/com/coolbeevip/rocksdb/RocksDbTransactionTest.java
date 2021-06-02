package com.coolbeevip.rocksdb;

import static com.coolbeevip.rocksdb.schema.MessageSchema.HOT_MESSAGE;
import static org.hamcrest.MatcherAssert.assertThat;

import com.coolbeevip.rocksdb.core.RocksDbAccessor;
import com.coolbeevip.rocksdb.core.RocksDbAccessor.RocksDbTransaction;
import com.coolbeevip.rocksdb.core.RocksDbFactory;
import com.coolbeevip.rocksdb.exception.DatabaseStorageException;
import com.coolbeevip.rocksdb.schema.MessageSchema;
import com.coolbeevip.rocksdb.schema.Message;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDBException;
import org.rocksdb.Snapshot;
import org.rocksdb.Status;
import org.rocksdb.TransactionOptions;

/**
 * @author zhanglei
 */

public class RocksDbTransactionTest {

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

  @Test
  public void readCommittedTest() throws Exception {
    Message record = Message.builder()
        .uuid(UUID.randomUUID().toString())
        .f1(UUID.randomUUID().toString())
        .build();

    RocksDbTransaction transaction = dbAccessor.startTransaction();
    try {
      // put a key INSIDE this transaction
      transaction.put(HOT_MESSAGE, record.getUuid(), record);

      // get a key OUTSIDE this transaction
      assertThat(dbAccessor.get(HOT_MESSAGE, record.getUuid()).isPresent(), Matchers.is(false));

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      throw e;
    }

    assertThat(dbAccessor.get(HOT_MESSAGE, record.getUuid()).get().getF1(),
        Matchers.is(record.getF1()));

  }

  @Test(expected = DatabaseStorageException.class)
  public void changeSameKeyFailOutsideTransactionTest() throws Exception {
    Message record = Message.builder()
        .uuid(UUID.randomUUID().toString())
        .f1(UUID.randomUUID().toString())
        .build();

    RocksDbTransaction transaction = dbAccessor.startTransaction();
    try {
      // put a key INSIDE this transaction
      transaction.put(HOT_MESSAGE, record.getUuid(), record);

      // throw exception when change same key OUTSIDE this transaction
      dbAccessor.put(HOT_MESSAGE, record.getUuid(), record);

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      throw e;
    }
  }

  @Test
  public void repeatableReadTest() throws Exception {
    Message record = Message.builder()
        .uuid(UUID.randomUUID().toString())
        .f1(UUID.randomUUID().toString()).build();

    // Set a snapshot at start of transaction by setting setSnapshot(true)
    TransactionOptions txnOptions = new TransactionOptions().setSetSnapshot(true);
    RocksDbTransaction transaction = dbAccessor.startTransaction(txnOptions);

    final Snapshot snapshot = transaction.getSnapshot();

    // put a key OUTSIDE this transaction
    dbAccessor.put(HOT_MESSAGE, record.getUuid(), record);

    try {
      ReadOptions readOptions = new ReadOptions();
      readOptions.setSnapshot(snapshot);
      transaction.getForUpdate(readOptions, HOT_MESSAGE, record.getUuid(), true);
      assert false;
    } catch (final DatabaseStorageException e) {
      RocksDBException exception = (RocksDBException) e.getCause();
      assertThat(exception.getStatus().getCode(), Matchers.is(Status.Code.Busy));
    }

    transaction.rollback();
  }

  @Test
  public void readCommittedSnapshotMultipleTest() throws Exception {
    Message recordX = Message.builder()
        .uuid(UUID.randomUUID().toString())
        .f1(UUID.randomUUID().toString())
        .build();

    Message recordY = Message.builder()
        .uuid(UUID.randomUUID().toString())
        .f1(UUID.randomUUID().toString())
        .build();

    // Set a snapshot at start of transaction by setting setSnapshot(true)
    TransactionOptions txnOptions = new TransactionOptions().setSetSnapshot(true);
    RocksDbTransaction transaction = dbAccessor.startTransaction(txnOptions);

    try {
      Snapshot snapshot = transaction.getSnapshot();
      ReadOptions readOptions = new ReadOptions();
      readOptions.setSnapshot(snapshot);
      transaction.get(readOptions, HOT_MESSAGE, recordX.getUuid());
      transaction.put(HOT_MESSAGE, recordX.getUuid(), recordX);

      // put a key OUTSIDE this transaction
      dbAccessor.put(HOT_MESSAGE, recordY.getUuid(), recordY);
      transaction.setSnapshot();
      transaction.setSavePoint();
      snapshot = transaction.getSnapshot();
      readOptions.setSnapshot(snapshot);

      Optional<Message> optional = transaction
          .getForUpdate(readOptions, HOT_MESSAGE, recordY.getUuid(), true);
      transaction.put(HOT_MESSAGE, recordY.getUuid(), recordY);

      // Decide we want to revert the last write from this transaction.
      transaction.rollbackToSavePoint();
      transaction.commit();
    } catch (Exception ex) {
      transaction.rollback();
    }
  }
}