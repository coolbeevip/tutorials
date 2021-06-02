package com.coolbeevip.rocksdb.core;

import java.util.Map;
import java.util.Objects;

/**
 * @author zhanglei
 */
public class ColumnEntry<K, V> implements Map.Entry<K, V> {

  private final K key;
  private final V value;

  private ColumnEntry(final K key, final V value) {
    this.key = key;
    this.value = value;
  }

  public static <K, V> ColumnEntry<K, V> create(final K key, final V value) {
    return new ColumnEntry<>(key, value);
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public V setValue(final V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof ColumnEntry)) {
      return false;
    }
    final ColumnEntry<?, ?> other = (ColumnEntry<?, ?>) obj;
    return Objects.equals(getKey(), other.getKey()) && Objects.equals(getValue(), other.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }
}
