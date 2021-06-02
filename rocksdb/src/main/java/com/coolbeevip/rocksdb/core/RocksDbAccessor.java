package com.coolbeevip.rocksdb.core;

import com.google.errorprone.annotations.MustBeClosed;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.rocksdb.ReadOptions;
import org.rocksdb.Snapshot;
import org.rocksdb.TransactionOptions;


public interface RocksDbAccessor extends AutoCloseable {

  <K,V> void put(RocksDbVariable<K,V> variable, K key, V value);

  <K,V> void delete(RocksDbVariable<K,V> variable, K key);

  <K, V> Optional<V> get(RocksDbVariable<K, V> variable, K key);

  <K, V> Map<K, V> getAll(RocksDbVariable<K, V> column);

  /**
   * Returns the last entry with a key less than or equal to the given key.
   *
   * @param column The column we want to query
   * @param key The requested key
   * @param <K> The key type of the column
   * @param <V> The value type of the column
   * @return The last entry with a key less than or equal to the given {@code key}
   */
  <K, V> Optional<ColumnEntry<K, V>> getFloorEntry(RocksDbVariable<K, V> column, K key);

  /**
   * Returns the first entry in the given column.
   *
   * @param column The column we want to query
   * @param <K> The key type of the column
   * @param <V> The value type of the column
   * @return The first entry in this column - the entry with the lowest key value
   */
  <K, V> Optional<ColumnEntry<K, V>> getFirstEntry(RocksDbVariable<K, V> column);

  /**
   * Returns the last entry in the given column.
   *
   * @param column The column we want to query
   * @param <K> The key type of the column
   * @param <V> The value type of the column
   * @return The last entry in this column - the entry with the greatest key value
   */
  <K, V> Optional<ColumnEntry<K, V>> getLastEntry(RocksDbVariable<K, V> column);

  /**
   * Returns the last key in the given column without loading the associated value.
   *
   * @param column The column we want to query
   * @param <K> The key type of the column
   * @param <V> The value type of the column
   * @return The last key in this column - the key with the greatest value
   */
  <K, V> Optional<K> getLastKey(RocksDbVariable<K, V> column);

  @MustBeClosed
  <K, V> Stream<ColumnEntry<K, V>> stream(RocksDbVariable<K, V> column);

  /**
   * Stream entries from a column between keys from and to fully inclusive.
   *
   * @param column the column to stream entries from
   * @param from the first key to return
   * @param to the last key to return
   * @param <K> the key type of the column
   * @param <V> the value type of the column
   * @return a Stream of entries between from and to (fully inclusive).
   */
  @MustBeClosed
  <K extends Comparable<K>, V> Stream<ColumnEntry<K, V>> stream(
      RocksDbVariable<K, V> column, K from, K to);

  RocksDbTransaction startTransaction(TransactionOptions txnOptions);
  RocksDbTransaction startTransaction();

  interface RocksDbTransaction extends AutoCloseable {

    <K,V> void put(RocksDbVariable<K,V> variable, K key, V value);

    <K, V> void put(RocksDbVariable<K, V> column, Map<K, V> data);

    <K,V> void delete(RocksDbVariable<K,V> variable, K key);

    <K, V> Optional<V> get(ReadOptions readOptions, RocksDbVariable<K, V> variable, K key);

    <K, V> Optional<V> getForUpdate(final ReadOptions readOptions,
        final RocksDbVariable<K, V> variable, K key,final boolean exclusive);

    Snapshot getSnapshot();

    void setSnapshot();

    void setSavePoint();

    void rollbackToSavePoint();

    void commit();

    void rollback();

    @Override
    void close();
  }
}
