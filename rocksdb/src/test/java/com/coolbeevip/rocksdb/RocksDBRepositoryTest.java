package com.coolbeevip.rocksdb;

import static org.hamcrest.MatcherAssert.assertThat;

import com.coolbeevip.rocksdb.repositoris.RocksDBRepository;
import com.coolbeevip.rocksdb.serialization.KryoSerialization;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.Test;

public class RocksDBRepositoryTest {


  @Test
  public void testCURD() {
    Serialization serialization = new KryoSerialization(RecordRow.class);
    RocksDBRepository repository = new RocksDBRepository("./",serialization);
    try {
      repository.open();

      RecordRow record = RecordRow.builder()
          .uuid(UUID.randomUUID().toString())
          .build();
      repository.save(record.getUuid(), record);

      assertThat(repository.find(record.getUuid()).get().getUuid(), Matchers.is(record.getUuid()));

      repository.delete(record.getUuid());

      assertThat(repository.find(record.getUuid()).isPresent(), Matchers.is(false));

    } finally {
      repository.close();
    }
  }
}