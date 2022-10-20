package com.coolbeevip.rocksdb.core;

import com.coolbeevip.rocksdb.RocksDbConfiguration;
import com.coolbeevip.rocksdb.exception.DatabaseStorageException;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.Cache;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.Env;
import org.rocksdb.LRUCache;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.Statistics;
import org.rocksdb.TransactionDB;
import org.rocksdb.TransactionDBOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author zhanglei
 */

@Slf4j
public class RocksDbFactory {

  static {
    try {
      RocksDB.loadLibrary();
      log.info("Load RocksDB library");
    } catch (final ExceptionInInitializerError e) {
      if (e.getCause() instanceof UnsupportedOperationException) {
        log.error("Unable to load RocksDB library", e);
        throw new IllegalStateException(
            "Unsupported platform detected. On Windows, ensure you have 64bit Java installed.");
      } else {
        throw e;
      }
    }
  }

  public static RocksDbAccessor create(
      final RocksDbConfiguration configuration,
      final Collection<RocksDbColumnFamily<?, ?>> columns)
      throws Exception {

    // 跟踪需要关闭的资源

    checkArgument(
        columns.stream().map(RocksDbColumnFamily::getCfId).distinct().count() == columns.size(),
        "Column IDs are not distinct");

    // Create options
    final TransactionDBOptions transactionDBOptions = new TransactionDBOptions();
    final RocksDbStats rocksDbStats = new RocksDbStats();
    final DBOptions dbOptions = createDBOptions(configuration, rocksDbStats.getStats());
    final LRUCache blockCache = new LRUCache(configuration.getCacheCapacity());
    final ColumnFamilyOptions columnFamilyOptions =
        createColumnFamilyOptions(configuration, blockCache);
    final List<AutoCloseable> resources =
        new ArrayList<>(
            Arrays.asList(transactionDBOptions, dbOptions, columnFamilyOptions, rocksDbStats,
                blockCache));

    List<ColumnFamilyDescriptor> columnDescriptors =
        createColumnFamilyDescriptors(columns, columnFamilyOptions);

    Map<BytesKey, RocksDbColumnFamily<?, ?>> columnsById =
        columns.stream()
            .collect(Collectors.toMap(RocksDbColumnFamily::getCfId, Function.identity()));

    try {
      // columnHandles will be filled when the db is opened
      final List<ColumnFamilyHandle> columnHandles = new ArrayList<>(columnDescriptors.size());
      final TransactionDB db =
          TransactionDB.open(
              dbOptions,
              transactionDBOptions,
              configuration.getDatabaseDir().toString(),
              columnDescriptors,
              columnHandles);

      final ImmutableMap.Builder<RocksDbColumnFamily<?, ?>, ColumnFamilyHandle> builder =
          ImmutableMap.builder();
      for (ColumnFamilyHandle columnHandle : columnHandles) {
        //final byte[] columnId = columnHandle.getName();
        final BytesKey columnId = new BytesKey(columnHandle.getName());
        final RocksDbColumnFamily<?, ?> rocksDbColumn = columnsById.get(columnId);
        if (rocksDbColumn != null) {
          // We need to check for null because the default column will not match a RocksDbColumn
          builder.put(rocksDbColumn, columnHandle);
        }
        resources.add(columnHandle);
      }
      final ImmutableMap<RocksDbColumnFamily<?, ?>, ColumnFamilyHandle> columnHandlesMap =
          builder.build();
      final ColumnFamilyHandle defaultHandle = getDefaultHandle(columnHandles);
      resources.add(db);

      rocksDbStats.registerMetrics(db);

      return new RocksDbInstance(db, defaultHandle, columnHandlesMap, resources);
    } catch (RocksDBException e) {
      throw new DatabaseStorageException(
          "Failed to open database at path: " + configuration.getDatabaseDir(), e);
    }
  }

  private static ColumnFamilyHandle getDefaultHandle(List<ColumnFamilyHandle> columnHandles) {
    return columnHandles.stream()
        .filter(
            handle -> {
              try {
                return Arrays.equals(handle.getName(), Schema.DEFAULT_COLUMN_ID);
              } catch (RocksDBException e) {
                throw new DatabaseStorageException("Unable to retrieve default column handle", e);
              }
            })
        .findFirst()
        .orElseThrow(() -> new DatabaseStorageException("No default column defined"));
  }

  private static DBOptions createDBOptions(
      final RocksDbConfiguration configuration, final Statistics stats) {
    final DBOptions options =
        new DBOptions()
            .setCreateIfMissing(true)
            .setBytesPerSync(1048576L)
            .setWalBytesPerSync(1048576L)
            .setIncreaseParallelism(Runtime.getRuntime().availableProcessors())
            .setMaxBackgroundJobs(configuration.getMaxBackgroundJobs())
            .setDbWriteBufferSize(configuration.getWriteBufferCapacity())
            .setMaxOpenFiles(configuration.getMaxOpenFiles())
            .setCreateMissingColumnFamilies(true)
            .setEnv(Env.getDefault().setBackgroundThreads(configuration.getBackgroundThreadCount()))
            .setStatistics(stats);
    if (configuration.optimizeForSmallDb()) {
      options.optimizeForSmallDb();
    }
    return options;
  }

  private static ColumnFamilyOptions createColumnFamilyOptions(
      final RocksDbConfiguration configuration, final Cache cache) {
    return new ColumnFamilyOptions()
        .setCompressionType(configuration.getCompressionType())
        .setBottommostCompressionType(configuration.getBottomMostCompressionType())
        .setTableFormatConfig(createBlockBasedTableConfig(cache));
  }

  private static List<ColumnFamilyDescriptor> createColumnFamilyDescriptors(
      final Collection<RocksDbColumnFamily<?, ?>> columns,
      final ColumnFamilyOptions columnFamilyOptions) {
    List<ColumnFamilyDescriptor> columnDescriptors =
        columns.stream()
            .map(
                col -> new ColumnFamilyDescriptor(col.getCfId().getArray(), columnFamilyOptions))
            .collect(Collectors.toList());
    columnDescriptors.add(
        new ColumnFamilyDescriptor(Schema.DEFAULT_COLUMN_ID, columnFamilyOptions));
    return columnDescriptors;
  }

  private static BlockBasedTableConfig createBlockBasedTableConfig(final Cache cache) {
    return new BlockBasedTableConfig()
        .setBlockCache(cache)
        .setCacheIndexAndFilterBlocks(true)
        .setFormatVersion(4); // Use the latest format version (only applies to new tables)
  }
}
