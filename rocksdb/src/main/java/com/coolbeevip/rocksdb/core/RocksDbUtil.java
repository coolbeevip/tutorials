package com.coolbeevip.rocksdb.core;

import lombok.extern.slf4j.Slf4j;
import org.rocksdb.RocksDB;

/**
 * @author zhanglei
 */

@Slf4j
class RocksDbUtil {

  public static void loadNativeLibrary() {
    try {
      RocksDB.loadLibrary();
    } catch (final ExceptionInInitializerError e) {
      if (e.getCause() instanceof UnsupportedOperationException) {
        log.info("Unable to load RocksDB library", e);
        throw new IllegalStateException(
            "Unsupported platform detected. On Windows, ensure you have 64bit Java installed.");
      } else {
        throw e;
      }
    }
  }
}
