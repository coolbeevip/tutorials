package com.coolbeevip.rocksdb;

public interface Serialization {

  <T> byte[] serialize(T object);

  <T> T deserialize(Class<T> clazz, byte[] bytes);
}
