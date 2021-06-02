package com.coolbeevip.rocksdb.core;

import com.coolbeevip.rocksdb.serialization.RocksDbSerializer;

/**
 * @author zhanglei
 */
public class RocksDbVariable<K, V> {

  private final BytesKey cfId;
//  private final byte[] key;
//  private final byte[] value;
  private final RocksDbSerializer<K> keySerializer;
  private final RocksDbSerializer<V> valueSerializer;

  private RocksDbVariable(final byte[] cfId,
      final RocksDbSerializer<K> keySerializer,
      final RocksDbSerializer<V> valueSerializer) {
    this.cfId = new BytesKey(cfId);
//    this.key = key;
//    this.value = value;
    this.keySerializer = keySerializer;
    this.valueSerializer = valueSerializer;
  }

  public BytesKey getCfId() {
    return cfId;
  }

  public byte[] getKey(K key){
    return this.keySerializer.serialize(key);
  }

  public byte[] getValue(V value){
    return this.valueSerializer.serialize(value);
  }

  public RocksDbSerializer<K> getKeySerializer() {
    return keySerializer;
  }

  public RocksDbSerializer<V> getValueSerializer() {
    return valueSerializer;
  }

  public V getValue(byte[] bytes) {
    return this.valueSerializer.deserialize(bytes);
  }

  public static RocksDbVariableBuilder builder() {
    return new RocksDbVariableBuilder();
  }

  public static class RocksDbVariableBuilder<K, V> {

    private byte[] cfId;
//    private K key;
//    private V value;
    private RocksDbSerializer<K> keySerializer;
    private RocksDbSerializer<V> valueSerializer;

    public RocksDbVariableBuilder cfId(String cfId) {
      this.cfId = cfId.getBytes();
      return this;
    }

    public RocksDbVariableBuilder keySerializer(RocksDbSerializer<K> keySerializer) {
      this.keySerializer = keySerializer;
      return this;
    }

    public RocksDbVariableBuilder valueSerializer(RocksDbSerializer<V> valueSerializer) {
      this.valueSerializer = valueSerializer;
      return this;
    }

//    public RocksDbVariableBuilder key(K key) {
//      this.key = key;
//      return this;
//    }
//
//    public RocksDbVariableBuilder value(V value) {
//      this.value = value;
//      return this;
//    }

    public RocksDbVariable<K, V> build() {
      return new RocksDbVariable<K, V>(this.cfId, keySerializer, valueSerializer);
    }
  }
}
