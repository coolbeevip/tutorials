package com.coolbeevip.rocksdb.core;

import com.coolbeevip.rocksdb.exception.ShuttingDownException;
import com.google.errorprone.annotations.MustBeClosed;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author zhanglei
 */

@Slf4j
class RocksDbIterator<TKey, TValue> implements Iterator<ColumnEntry<TKey, TValue>>, AutoCloseable {

  private final RocksDbColumnFamily<TKey, TValue> column;
  private final RocksIterator rocksIterator;
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final Predicate<TKey> continueTest;
  private final Supplier<Boolean> isDatabaseClosed;

  private RocksDbIterator(
      final RocksDbColumnFamily<TKey, TValue> column,
      final RocksIterator rocksIterator,
      final Predicate<TKey> continueTest,
      final Supplier<Boolean> isDatabaseClosed) {
    this.column = column;
    this.rocksIterator = rocksIterator;
    this.continueTest = continueTest;
    this.isDatabaseClosed = isDatabaseClosed;
  }

  @MustBeClosed
  public static <K, V> RocksDbIterator<K, V> create(
      final RocksDbColumnFamily<K, V> column,
      final RocksIterator rocksIt,
      final Predicate<K> continueTest,
      final Supplier<Boolean> isDatabaseClosed) {
    return new RocksDbIterator<>(column, rocksIt, continueTest, isDatabaseClosed);
  }

  @Override
  public boolean hasNext() {
    assertOpen();
    return rocksIterator.isValid()
        && continueTest.test(column.getKeySerializer().deserialize(rocksIterator.key()));
  }

  @Override
  public ColumnEntry<TKey, TValue> next() {
    assertOpen();
    try {
      rocksIterator.status();
    } catch (final RocksDBException e) {
      log.error("RocksDbEntryIterator encountered a problem while iterating.", e);
    }
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    final TKey key = column.getKeySerializer().deserialize(rocksIterator.key());
    final TValue value = column.getValueSerializer().deserialize(rocksIterator.value());
    final ColumnEntry<TKey, TValue> entry = ColumnEntry.create(key, value);
    rocksIterator.next();
    return entry;
  }

  @MustBeClosed
  public Stream<ColumnEntry<TKey, TValue>> toStream() {
    assertOpen();
    final Spliterator<ColumnEntry<TKey, TValue>> split =
        Spliterators.spliteratorUnknownSize(
            this,
            Spliterator.IMMUTABLE
                | Spliterator.DISTINCT
                | Spliterator.NONNULL
                | Spliterator.ORDERED
                | Spliterator.SORTED);

    return StreamSupport.stream(split, false).onClose(this::close);
  }

  private void assertOpen() {
    if (this.isDatabaseClosed.get()) {
      throw new ShuttingDownException();
    }
    if (closed.get()) {
      throw new IllegalStateException(
          "Attempt to update a closed " + this.getClass().getSimpleName());
    }
  }

  @Override
  public void close() {
    if (closed.compareAndSet(false, true)) {
      rocksIterator.close();
    }
  }
}
