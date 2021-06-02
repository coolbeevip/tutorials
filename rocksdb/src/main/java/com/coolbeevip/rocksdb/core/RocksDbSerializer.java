package com.coolbeevip.rocksdb.core;

/**
 * @author zhanglei
 */

public interface RocksDbSerializer<T> {

  T deserialize(final byte[] data);

  byte[] serialize(final T value);

//  Class<T> getClz();
}
