package com.coolbeevip.rocksdb.exception;

public class DatabaseStorageException extends RuntimeException {

  public DatabaseStorageException(final String s) {
    super(s);
  }

  public DatabaseStorageException(final String s, final Throwable cause) {
    super(s, cause);
  }
}
