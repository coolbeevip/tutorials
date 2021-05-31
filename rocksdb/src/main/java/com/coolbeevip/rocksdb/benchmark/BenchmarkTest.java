package com.coolbeevip.rocksdb.benchmark;

import com.coolbeevip.rocksdb.RecordRow;
import com.coolbeevip.rocksdb.Serialization;
import com.coolbeevip.rocksdb.repositoris.RocksDBRepository;
import com.coolbeevip.rocksdb.serialization.KryoSerialization;
import java.util.LinkedList;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@Slf4j
@State(Scope.Benchmark)
public class BenchmarkTest {

  Serialization serialization = new KryoSerialization(RecordRow.class);
  RocksDBRepository repository = new RocksDBRepository("./", serialization);

  @State(Scope.Benchmark)
  public static class BenchmarkState {

    LinkedList<String> keys = new LinkedList<>();
  }

  public BenchmarkTest() {
    repository.open();
  }

  @TearDown
  public void tearDown(BenchmarkState state) {
    state.keys.stream().parallel().forEach(key -> {
      try {
        repository.delete(key);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    });
  }

  @Benchmark
  public void put(BenchmarkState state) {
    RecordRow record = RecordRow.builder()
        .uuid(UUID.randomUUID().toString())
        .build();
    repository.save(record.getUuid(), record);
    state.keys.add(record.getUuid());
  }

  public static void main(String[] args) throws Exception {
    org.openjdk.jmh.Main.main(args);
  }
}
