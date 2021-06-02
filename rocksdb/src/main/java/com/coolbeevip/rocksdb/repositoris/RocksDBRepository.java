package com.coolbeevip.rocksdb.repositoris;

import com.coolbeevip.rocksdb.KVRepository;
import com.coolbeevip.rocksdb.schema.RecordRow;
import com.coolbeevip.rocksdb.serialization.RocksDbSerializer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.CompactionStyle;
import org.rocksdb.CompressionType;
import org.rocksdb.Options;
import org.rocksdb.RateLimiter;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.Statistics;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;
import org.rocksdb.util.SizeUnit;

@Slf4j
public class RocksDBRepository implements KVRepository<String, RecordRow> {

  static {
    RocksDB.loadLibrary();
  }

  private final static String FILE_NAME = "rocksdb-data";
  private final File baseDir;
  private final RocksDbSerializer<String> keySerializer;
  private final RocksDbSerializer<RecordRow> valueSerializer;
  private final Statistics stats = new Statistics();
  private final RateLimiter rateLimiter = new RateLimiter(10000000, 10000, 10);
  private RocksDB db;

  public RocksDBRepository(String path, RocksDbSerializer<String> keySerializer,
      RocksDbSerializer<RecordRow> valueSerializer) {
    this.keySerializer = keySerializer;
    this.valueSerializer = valueSerializer;
    this.baseDir = new File(path, FILE_NAME);
  }

  @Override
  public boolean put(String key, RecordRow value) {
    return put(null, key, value);
  }

  @Override
  public boolean put(WriteOptions writeOpts, String key, RecordRow value) {
    try {
      if (writeOpts != null) {
        db.put(writeOpts, this.keySerializer.serialize(key), this.valueSerializer.serialize(value));
      } else {
        db.put(this.keySerializer.serialize(key), this.valueSerializer.serialize(value));
      }
    } catch (RocksDBException e) {
      log.error("Error saving entry. Cause: '{}', message: '{}'", e.getCause(), e.getMessage());
      return false;
    }
    return true;
  }

  @Override
  public boolean saveBatch(Map<String, RecordRow> records) {
    try (final WriteOptions writeOpt = new WriteOptions()) {
      try (final WriteBatch batch = new WriteBatch()) {
        try {
          Iterator<String> it = records.keySet().iterator();
          while (it.hasNext()) {
            String key = it.next();
            RecordRow record = records.get(key);
            batch.put(this.keySerializer.serialize(key),
                this.valueSerializer.serialize(record));
          }
          db.write(writeOpt, batch);
        } catch (RocksDBException e) {
          log.error("Error batch saving entry. Cause: '{}', message: '{}'", e.getCause(),
              e.getMessage());
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public Optional<RecordRow> get(String key) {
    RecordRow value = null;
    try {
      byte[] bytes = db.get(keySerializer.serialize(key));
      if (bytes != null) {
        value = this.valueSerializer.deserialize(bytes);
      }
    } catch (RocksDBException e) {
      log.error(
          "Error retrieving the entry with key: {}, cause: {}, message: {}",
          key,
          e.getCause(),
          e.getMessage()
      );
    }
    return Optional.ofNullable(value);
  }

  @Override
  public Optional<List<RecordRow>> get(List<String> keys) {
    List<RecordRow> recordRows = null;
    try {
      List<byte[]> bytes = db.multiGetAsList(
          keys.stream().map(k -> keySerializer.serialize(k)).collect(Collectors.toList()));
      if (bytes != null) {
        recordRows = bytes.stream().map(v -> valueSerializer.deserialize(v))
            .collect(Collectors.toList());
      }
    } catch (RocksDBException e) {
      log.error(
          "Error retrieving the entry with keys: {}, cause: {}, message: {}",
          keys.stream().collect(Collectors.joining(",")),
          e.getCause(),
          e.getMessage()
      );
    }
    return Optional.ofNullable(recordRows);
  }

  @Override
  public boolean delete(String key) {
    try {
      db.delete(keySerializer.serialize(key));
    } catch (RocksDBException e) {
      log.error("Error deleting entry, cause: '{}', message: '{}'", e.getCause(), e.getMessage());
      return false;
    }
    return true;
  }

  @Override
  public RocksIterator iterator() {
    return db.newIterator();
  }

  @Override
  public Optional<String> status() {
    try {
      return Optional.ofNullable(db.getProperty("rocksdb.stats"));
    } catch (RocksDBException e) {
      log.error("Error get rocksdb stats, cause: '{}', message: '{}'", e.getCause(),
          e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public Statistics getStatistics() {
    return stats;
  }

  @Override
  public void open() {
    final Options options = new Options();
    options.setCreateIfMissing(true)
        .setStatistics(stats)
        .setRateLimiter(rateLimiter)
        .setWriteBufferSize(8 * SizeUnit.KB)
        .setMaxWriteBufferNumber(3)
        .setMaxBackgroundJobs(10)
        .setCompressionType(CompressionType.SNAPPY_COMPRESSION)
        .setCompactionStyle(CompactionStyle.UNIVERSAL);
    try {
      Files.createDirectories(baseDir.getParentFile().toPath());
      Files.createDirectories(baseDir.getAbsoluteFile().toPath());
      db = RocksDB.open(options, baseDir.getAbsolutePath());
      log.info("RocksDB initialized");
    } catch (IOException | RocksDBException e) {
      log.error("Error initializing RocksDB. Exception: '{}', message: '{}'", e.getCause(),
          e.getMessage(), e);
    }
  }

  @Override
  public void close() {
    this.db.close();
  }
}