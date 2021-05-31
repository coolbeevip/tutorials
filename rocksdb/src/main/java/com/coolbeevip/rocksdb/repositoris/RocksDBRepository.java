package com.coolbeevip.rocksdb.repositoris;

import com.coolbeevip.rocksdb.KVRepository;
import com.coolbeevip.rocksdb.RecordRow;
import com.coolbeevip.rocksdb.Serialization;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

@Slf4j
public class RocksDBRepository implements KVRepository<String, RecordRow> {

  private final static String FILE_NAME = "rocksdb-data";
  private final File baseDir;
  private final Serialization serialization;
  RocksDB db;

  public RocksDBRepository(String path, Serialization serialization) {
    this.serialization = serialization;
    this.baseDir = new File(path, FILE_NAME);
  }

  @Override
  public boolean save(String key, RecordRow value) {
    if (log.isDebugEnabled()) {
      log.debug("saving value '{}' with key '{}'", value, key);
    }

    try {
      db.put(key.getBytes(), this.serialization.serialize(value));
    } catch (RocksDBException e) {
      log.error("Error saving entry. Cause: '{}', message: '{}'", e.getCause(), e.getMessage());
      return false;
    }
    return true;
  }

  @Override
  public Optional<RecordRow> find(String key) {
    RecordRow value = null;
    try {
      byte[] bytes = db.get(key.getBytes());
      if (bytes != null) {
        value = this.serialization.deserialize(RecordRow.class, bytes);
      }
    } catch (RocksDBException e) {
      log.error(
          "Error retrieving the entry with key: {}, cause: {}, message: {}",
          key,
          e.getCause(),
          e.getMessage()
      );
    }
    if (log.isDebugEnabled()) {
      log.debug("finding key '{}' returns '{}'", key, value);
    }

    return value != null ? Optional.of(value) : Optional.empty();
  }

  @Override
  public boolean delete(String key) {
    if (log.isDebugEnabled()) {
      log.debug("deleting key '{}'", key);
    }

    try {
      db.delete(key.getBytes());
    } catch (RocksDBException e) {
      log.error("Error deleting entry, cause: '{}', message: '{}'", e.getCause(), e.getMessage());
      return false;
    }
    return true;
  }

  @Override
  public void open() {
    RocksDB.loadLibrary();
    final Options options = new Options();
    options.setCreateIfMissing(true);
    try {
      Files.createDirectories(baseDir.getParentFile().toPath());
      Files.createDirectories(baseDir.getAbsoluteFile().toPath());
      db = RocksDB.open(options, baseDir.getAbsolutePath());
      log.info("RocksDB initialized");
    } catch (IOException | RocksDBException e) {
      log.error("Error initializng RocksDB. Exception: '{}', message: '{}'", e.getCause(),
          e.getMessage(), e);
    }
  }

  @Override
  public void close() {
    this.db.close();
  }
}