package com.coolbeevip.rocksdb.core;

import com.coolbeevip.rocksdb.exception.DatabaseStorageException;
import com.coolbeevip.rocksdb.exception.ShuttingDownException;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.MustBeClosed;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.rocksdb.AbstractRocksIterator;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.Snapshot;
import org.rocksdb.TransactionDB;
import org.rocksdb.TransactionOptions;
import org.rocksdb.WriteOptions;

public class RocksDbInstance implements RocksDbAccessor {

  private final TransactionDB db;
  private final ColumnFamilyHandle defaultHandle;
  private final ImmutableMap<RocksDbVariable<?, ?>, ColumnFamilyHandle> columnHandles;
  private final List<AutoCloseable> resources;
  private final Set<Transaction> openTransactions = new HashSet<>();

  private final AtomicBoolean closed = new AtomicBoolean(false);

  RocksDbInstance(
      final TransactionDB db,
      final ColumnFamilyHandle defaultHandle,
      final ImmutableMap<RocksDbVariable<?, ?>, ColumnFamilyHandle> columnHandles,
      final List<AutoCloseable> resources) {
    this.db = db;
    this.defaultHandle = defaultHandle;
    this.columnHandles = columnHandles;
    this.resources = resources;
  }

  @Override
  public <K, V> void put(RocksDbVariable<K, V> column, K key, V value) {
    try {
      final ColumnFamilyHandle handle = columnHandles.get(column);
      db.put(handle, column.getKey(key), column.getValue(value));
    } catch (RocksDBException e) {
      throw new DatabaseStorageException("Failed to put variable", e);
    }
  }

  @Override
  public <K, V> void delete(RocksDbVariable<K, V> variable, K key) {
    try {
      final ColumnFamilyHandle handle = columnHandles.get(variable);
      db.delete(handle, variable.getKey(key));
    } catch (RocksDBException e) {
      throw new DatabaseStorageException("Failed to delete variable", e);
    }
  }

  @Override
  public <K, V> Optional<V> get(RocksDbVariable<K, V> column, K key) {
    assertOpen();
    final ColumnFamilyHandle handle = columnHandles.get(column);
    final byte[] keyBytes = column.getKeySerializer().serialize(key);
    try {
      return Optional.ofNullable(db.get(handle, keyBytes))
          .map(data -> column.getValueSerializer().deserialize(data));
    } catch (RocksDBException e) {
      throw new DatabaseStorageException("Failed to get value", e);
    }
  }

  @Override
  public <K, V> Map<K, V> getAll(RocksDbVariable<K, V> column) {
    assertOpen();
    try (final Stream<ColumnEntry<K, V>> stream = stream(column)) {
      return stream.collect(Collectors.toMap(ColumnEntry::getKey, ColumnEntry::getValue));
    }
  }

  @Override
  public <K, V> Optional<ColumnEntry<K, V>> getFloorEntry(RocksDbVariable<K, V> column, final K key) {
    assertOpen();
    final byte[] keyBytes = column.getKeySerializer().serialize(key);
    final Consumer<RocksIterator> setupIterator = it -> it.seekForPrev(keyBytes);
    try (final Stream<ColumnEntry<K, V>> stream = createStream(column, setupIterator)) {
      return stream.findFirst();
    }
  }

  @Override
  public <K, V> Optional<ColumnEntry<K, V>> getFirstEntry(final RocksDbVariable<K, V> column) {
    assertOpen();
    try (final Stream<ColumnEntry<K, V>> stream =
        createStream(column, AbstractRocksIterator::seekToFirst)) {
      return stream.findFirst();
    }
  }

  @Override
  public <K, V> Optional<ColumnEntry<K, V>> getLastEntry(RocksDbVariable<K, V> column) {
    assertOpen();
    try (final Stream<ColumnEntry<K, V>> stream =
        createStream(column, AbstractRocksIterator::seekToLast)) {
      return stream.findFirst();
    }
  }

  @Override
  public <K, V> Optional<K> getLastKey(final RocksDbVariable<K, V> column) {
    assertOpen();
    final ColumnFamilyHandle handle = columnHandles.get(column);
    try (final RocksIterator rocksDbIterator = db.newIterator(handle)) {
      rocksDbIterator.seekToLast();
      return rocksDbIterator.isValid()
          ? Optional.of(column.getKeySerializer().deserialize(rocksDbIterator.key()))
          : Optional.empty();
    }
  }

  @Override
  @MustBeClosed
  public <K, V> Stream<ColumnEntry<K, V>> stream(RocksDbVariable<K, V> column) {
    assertOpen();
    return createStream(column, RocksIterator::seekToFirst);
  }

  @Override
  @MustBeClosed
  public <K extends Comparable<K>, V> Stream<ColumnEntry<K, V>> stream(
      final RocksDbVariable<K, V> column, final K from, final K to) {
    assertOpen();
    return createStream(
        column,
        iter -> iter.seek(column.getKeySerializer().serialize(from)),
        key -> key.compareTo(to) <= 0);
  }

  @Override
  @MustBeClosed
  public RocksDbTransaction startTransaction(TransactionOptions transactionOptions) {
    assertOpen();
    Transaction tx = new Transaction(db, transactionOptions, defaultHandle, columnHandles,
        openTransactions::remove);
    openTransactions.add(tx);
    return tx;
  }

  @Override
  @MustBeClosed
  public synchronized RocksDbTransaction startTransaction() {
    return startTransaction(null);
  }

  @MustBeClosed
  private <K, V> Stream<ColumnEntry<K, V>> createStream(
      RocksDbVariable<K, V> column, Consumer<RocksIterator> setupIterator) {
    return createStream(column, setupIterator, key -> true);
  }

  @SuppressWarnings("MustBeClosedChecker")
  @MustBeClosed
  private <K, V> Stream<ColumnEntry<K, V>> createStream(
      RocksDbVariable<K, V> column,
      Consumer<RocksIterator> setupIterator,
      Predicate<K> continueTest) {
    final ColumnFamilyHandle handle = columnHandles.get(column);
    final RocksIterator rocksDbIterator = db.newIterator(handle);
    setupIterator.accept(rocksDbIterator);
    return RocksDbIterator.create(column, rocksDbIterator, continueTest, closed::get).toStream();
  }

  @Override
  public synchronized void close() throws Exception {
    if (closed.compareAndSet(false, true)) {
      for (Transaction openTransaction : openTransactions) {
        openTransaction.closeViaDatabase();
      }
      db.syncWal();
      for (final AutoCloseable resource : resources) {
        resource.close();
      }
    }
  }

  private void assertOpen() {
    if (closed.get()) {
      throw new ShuttingDownException();
    }
  }

  public static class Transaction implements RocksDbTransaction {

    private final ColumnFamilyHandle defaultHandle;
    private final ImmutableMap<RocksDbVariable<?, ?>, ColumnFamilyHandle> columnHandles;
    private final org.rocksdb.Transaction rocksDbTx;
    private final WriteOptions writeOptions;
    private final TransactionOptions transactionOptions;

    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean closedViaDatabase = new AtomicBoolean(false);
    private final Consumer<Transaction> onClosed;
    private boolean closed = false;

    private Transaction(
        final TransactionDB db,
        final TransactionOptions transactionOptions,
        final ColumnFamilyHandle defaultHandle,
        final ImmutableMap<RocksDbVariable<?, ?>, ColumnFamilyHandle> columnHandles,
        final Consumer<Transaction> onClosed) {
      this.defaultHandle = defaultHandle;
      this.columnHandles = columnHandles;
      this.writeOptions = new WriteOptions();
      this.transactionOptions = transactionOptions;
      this.rocksDbTx = transactionOptions != null ? db.beginTransaction(writeOptions,
          transactionOptions) : db.beginTransaction(writeOptions);
      this.onClosed = onClosed;
    }

    @Override
    public <K, V> void put(RocksDbVariable<K, V> column, K key, V value) {
      applyUpdate(
          () -> {
            final byte[] keyBytes = column.getKeySerializer().serialize(key);
            final byte[] valueBytes = column.getValueSerializer().serialize(value);
            final ColumnFamilyHandle handle = columnHandles.get(column);
            try {
              rocksDbTx.put(handle, keyBytes, valueBytes);
            } catch (RocksDBException e) {
              throw new DatabaseStorageException("Failed to put column data", e);
            }
          });
    }

    @Override
    public <K, V> void put(RocksDbVariable<K, V> column, Map<K, V> data) {
      applyUpdate(
          () -> {
            final ColumnFamilyHandle handle = columnHandles.get(column);
            for (Map.Entry<K, V> kvEntry : data.entrySet()) {
              final byte[] key = column.getKeySerializer().serialize(kvEntry.getKey());
              final byte[] value = column.getValueSerializer().serialize(kvEntry.getValue());
              try {
                rocksDbTx.put(handle, key, value);
              } catch (RocksDBException e) {
                throw new DatabaseStorageException("Failed to put column data", e);
              }
            }
          });
    }

    @Override
    public <K, V> void delete(RocksDbVariable<K, V> column, K key) {
      applyUpdate(
          () -> {
            final ColumnFamilyHandle handle = columnHandles.get(column);
            try {
              rocksDbTx.delete(handle, column.getKeySerializer().serialize(key));
            } catch (RocksDBException e) {
              throw new DatabaseStorageException("Failed to delete key", e);
            }
          });
    }

    @Override
    public <K, V> Optional<V> get(ReadOptions readOptions, RocksDbVariable<K, V> variable, K key) {
      final ColumnFamilyHandle handle = columnHandles.get(variable);
      try {
        return Optional.ofNullable(rocksDbTx.get(handle, readOptions, variable.getKey(key)))
            .map(data -> variable.getValue(data));
      } catch (RocksDBException e) {
        throw new DatabaseStorageException("Failed to get value", e);
      }
    }

    @Override
    public <K, V> Optional<V> getForUpdate(final ReadOptions readOptions,
        final RocksDbVariable<K, V> variable, K key, final boolean exclusive) {
      final ColumnFamilyHandle handle = columnHandles.get(variable);
      try {
        return Optional.ofNullable(
            rocksDbTx.getForUpdate(readOptions, handle, variable.getKey(key), exclusive))
            .map(data -> variable.getValue(data));
      } catch (RocksDBException e) {
        throw new DatabaseStorageException("Failed to get value", e);
      }
    }

    @Override
    public Snapshot getSnapshot() {
      return this.rocksDbTx.getSnapshot();
    }

    @Override
    public void setSnapshot() {
      this.rocksDbTx.setSnapshot();
    }

    @Override
    public void setSavePoint() {
      try {
        this.rocksDbTx.setSavePoint();
      } catch (RocksDBException e) {
        throw new DatabaseStorageException(e.getMessage(), e);
      }
    }

    @Override
    public void rollbackToSavePoint() {
      try {
        this.rocksDbTx.rollbackToSavePoint();
      } catch (RocksDBException e) {
        throw new DatabaseStorageException(e.getMessage(), e);
      }
    }

    @Override
    public void commit() {
      applyUpdate(
          () -> {
            try {
              this.rocksDbTx.commit();
            } catch (RocksDBException e) {
              throw new DatabaseStorageException("Failed to commit transaction", e);
            } finally {
              close();
            }
          });
    }

    @Override
    public void rollback() {
      applyUpdate(
          () -> {
            try {
              this.rocksDbTx.commit();
            } catch (RocksDBException e) {
              throw new DatabaseStorageException("Failed to commit transaction", e);
            } finally {
              close();
            }
          });
    }

    private void applyUpdate(final Runnable operation) {
      lock.lock();
      try {
        assertOpen();
        operation.run();
      } finally {
        lock.unlock();
      }
    }

    private void assertOpen() {
      if (closed) {
        if (closedViaDatabase.get()) {
          throw new ShuttingDownException();
        } else {
          throw new IllegalStateException("Attempt to update a closed transaction");
        }
      }
    }

    private void closeViaDatabase() {
      closedViaDatabase.set(true);
      close();
    }

    @Override
    public void close() {
      lock.lock();
      try {
        if (!closed) {
          closed = true;
          onClosed.accept(this);
          writeOptions.close();
          rocksDbTx.close();
        }
      } finally {
        lock.unlock();
      }
    }
  }
}
