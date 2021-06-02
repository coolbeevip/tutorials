package com.coolbeevip.rocksdb;

import com.coolbeevip.rocksdb.schema.RecordRow;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.rocksdb.RocksIterator;
import org.rocksdb.Statistics;
import org.rocksdb.WriteOptions;

public interface KVRepository<K, V> {

  boolean put(K key, V value);

  boolean put(WriteOptions writeOpts, K key, V value);

  boolean saveBatch(Map<String, RecordRow> records);

  Optional<V> get(K key);

  Optional<List<V>> get(List<K> keys);

  boolean delete(K key);

  RocksIterator iterator();

  Optional<String> status();

  Statistics getStatistics();

  void open();

  void close();

}
