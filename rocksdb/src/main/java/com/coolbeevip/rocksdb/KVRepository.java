package com.coolbeevip.rocksdb;

import java.util.Optional;

public interface KVRepository<K,V> {

  boolean save(K key, V value);

  Optional<V> find(K key);

  boolean delete(K key);

  void open();

  void close();

}
