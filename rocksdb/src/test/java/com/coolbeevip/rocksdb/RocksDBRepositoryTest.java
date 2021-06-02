package com.coolbeevip.rocksdb;

import static org.hamcrest.MatcherAssert.assertThat;

import com.coolbeevip.rocksdb.repositoris.RocksDBRepository;
import com.coolbeevip.rocksdb.schema.RecordRow;
import com.coolbeevip.rocksdb.serialization.KryoSerializer;
import com.coolbeevip.rocksdb.serialization.RocksDbSerializer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rocksdb.HistogramData;
import org.rocksdb.HistogramType;
import org.rocksdb.RocksIterator;

@Slf4j
public class RocksDBRepositoryTest {

  static RocksDBRepository repository;

  @BeforeClass
  public static void beforeClass() {
    RocksDbSerializer<String> keySerializer = new KryoSerializer(String.class);
    RocksDbSerializer<RecordRow> valueSerializer = new KryoSerializer(RecordRow.class);
    repository = new RocksDBRepository("./", keySerializer, valueSerializer);
    repository.open();
  }

  @AfterClass
  public static void afterClass() {
    repository.close();
  }

  /**
   * 测试 put, get, delete
   */
  @Test
  public void testCURD() {
    assertThat(repository.status().isPresent(), Matchers.is(true));

    RecordRow record = RecordRow.builder()
        .uuid(UUID.randomUUID().toString())
        .build();
    repository.put(record.getUuid(), record);

    assertThat(repository.get(record.getUuid()).get().getUuid(), Matchers.is(record.getUuid()));

    repository.delete(record.getUuid());

    assertThat(repository.get(record.getUuid()).isPresent(), Matchers.is(false));
  }

  /**
   * 测试批量写入
   */
  @Test
  public void testSaveBatch() {
    long maxSize = 1000;
    Map<String, RecordRow> records = new HashMap<>();
    for (int i = 0; i < maxSize; i++) {
      RecordRow record = RecordRow.builder()
          .uuid(UUID.randomUUID().toString())
          .build();
      records.put(record.getUuid(), record);
    }
    repository.saveBatch(records);

    assertThat(records.entrySet().stream()
            .filter(record -> repository.get(record.getKey()).isPresent()).count(),
        Matchers.is(maxSize));
  }

  /**
   * 测试批量读取
   */
  @Test
  public void testMultiGet() {
    long maxSize = 100;
    Map<String, RecordRow> records = new HashMap<>();
    for (int i = 0; i < maxSize; i++) {
      RecordRow record = RecordRow.builder()
          .uuid(UUID.randomUUID().toString())
          .build();
      records.put(record.getUuid(), record);
    }
    repository.saveBatch(records);
    Optional<List<RecordRow>> optionalRecordRowList = repository
        .get(records.keySet().stream().collect(Collectors.toList()));
    assertThat(optionalRecordRowList.isPresent(), Matchers.is(true));
    assertThat(optionalRecordRowList.get().size(), Matchers.is(records.size()));
    optionalRecordRowList.get().stream().forEach(record -> {
      assertThat(records.get(record.getUuid()).getUuid(), Matchers.is(record.getUuid()));
    });
  }


  @Test
  public void repeatTest() {
    String uuid = UUID.randomUUID().toString();
    RecordRow record = RecordRow.builder()
        .uuid(uuid)
        .f1("tom")
        .build();
    repository.put(record.getUuid(), record);
    assertThat(repository.get(record.getUuid()).get().getF1(), Matchers.is(record.getF1()));

    repository.delete(record.getUuid());
    assertThat(repository.get(record.getUuid()).isPresent(), Matchers.is(false));

    record = RecordRow.builder()
        .uuid(uuid)
        .f1("jack")
        .build();
    repository.put(record.getUuid(), record);
    assertThat(repository.get(record.getUuid()).get().getF1(), Matchers.is(record.getF1()));
  }

  @Test
  public void histogramDataTest() {
    long maxSize = 100;
    Map<String, RecordRow> records = new HashMap<>();
    for (int i = 0; i < maxSize; i++) {
      RecordRow record = RecordRow.builder()
          .uuid(UUID.randomUUID().toString())
          .build();
      records.put(record.getUuid(), record);
    }
    repository.saveBatch(records);
    for (final HistogramType histogramType : HistogramType.values()) {
      if (histogramType != HistogramType.HISTOGRAM_ENUM_MAX) {
        HistogramData data = repository.getStatistics().getHistogramData(histogramType);
        assertThat(data, Matchers.is(Matchers.notNullValue()));
        log.info("{}={}", histogramType.name(), data.getCount());
      }
    }
  }

  @SneakyThrows
  @Test
  public void iteratorTest() {
    try (final RocksIterator iterator = repository.iterator()) {
      boolean seekToFirstPassed = false;
      for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
        iterator.status();
        assertThat(iterator.key(), Matchers.is(Matchers.notNullValue()));
        assertThat(iterator.value(), Matchers.is(Matchers.notNullValue()));
        seekToFirstPassed = true;
      }
      assertThat(seekToFirstPassed, Matchers.is(true));

      boolean seekToLastPassed = false;
      for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
        iterator.status();
        assertThat(iterator.key(), Matchers.is(Matchers.notNullValue()));
        assertThat(iterator.value(), Matchers.is(Matchers.notNullValue()));
        seekToLastPassed = true;
      }
      assertThat(seekToLastPassed, Matchers.is(true));
    }
  }

//  @Test
//  public void testReadCommitted() {
//    try (final Options options = new Options()
//        .setCreateIfMissing(true);
//        final TransactionDBOptions txnDbOptions = new TransactionDBOptions();
//        final TransactionDB txnDb =
//            TransactionDB.open(options, txnDbOptions, dbPath)) {
//
//      try (final WriteOptions writeOptions = new WriteOptions();
//          final ReadOptions readOptions = new ReadOptions()) {
//        readCommitted(txnDb, writeOptions, readOptions);
//      }
//    }
//  }
}