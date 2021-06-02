package com.coolbeevip.rocksdb;

import java.nio.file.Path;
import org.rocksdb.CompressionType;

/**
 * @author zhanglei
 */

public class RocksDbConfiguration {

  public static final int DEFAULT_MAX_OPEN_FILES = 128;
  public static final int DEFAULT_MAX_BACKGROUND_JOBS = 6;
  public static final int DEFAULT_BACKGROUND_THREAD_COUNT = 6;
  public static final long DEFAULT_CACHE_CAPACITY = 8 << 20;
  public static final long DEFAULT_WRITE_BUFFER_CAPACITY = 128 << 20;
  private static final boolean DEFAULT_OPTIMISE_FOR_SMALL_DB = false;

  private int maxOpenFiles = DEFAULT_MAX_OPEN_FILES;
  private int maxBackgroundJobs = DEFAULT_MAX_BACKGROUND_JOBS;
  private int backgroundThreadCount = DEFAULT_BACKGROUND_THREAD_COUNT;
  private long cacheCapacity = DEFAULT_CACHE_CAPACITY;
  private long writeBufferCapacity = DEFAULT_WRITE_BUFFER_CAPACITY;
  private boolean optimizeForSmallDb = DEFAULT_OPTIMISE_FOR_SMALL_DB;
  private CompressionType compressionType = CompressionType.NO_COMPRESSION;
  private CompressionType bottomMostCompressionType = CompressionType.NO_COMPRESSION;

  final private Path databaseDir;

  public RocksDbConfiguration(final Path databaseDir) {
    this.databaseDir = databaseDir;
  }

  public static RocksDbConfiguration hotDefaults(final Path databaseDir) {

    final RocksDbConfiguration config = new RocksDbConfiguration(databaseDir);
    config.optimizeForSmallDb = true;
    return config;
  }

  public static RocksDbConfiguration archiveDefaults(final Path databaseDir) {
    return new RocksDbConfiguration(databaseDir);
  }

  public Path getDatabaseDir() {
    return databaseDir;
  }

  public int getMaxOpenFiles() {
    return maxOpenFiles;
  }

  public int getMaxBackgroundJobs() {
    return maxBackgroundJobs;
  }

  public int getBackgroundThreadCount() {
    return backgroundThreadCount;
  }

  public long getCacheCapacity() {
    return cacheCapacity;
  }

  public long getWriteBufferCapacity() {
    return writeBufferCapacity;
  }

  public CompressionType getCompressionType() {
    return compressionType;
  }

  public CompressionType getBottomMostCompressionType() {
    return bottomMostCompressionType;
  }

  public boolean optimizeForSmallDb() {
    return optimizeForSmallDb;
  }
}
