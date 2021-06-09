package com.coolbeevip.rocksdb.schema;


import com.coolbeevip.rocksdb.core.RocksDbColumnFamily;
import com.coolbeevip.rocksdb.core.RocksDbSerializer;
import com.coolbeevip.rocksdb.core.Schema;
import com.coolbeevip.rocksdb.serializer.KryoSerializer;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhanglei
 */
public class MessageSchema implements Schema {

  public static final MessageSchema INSTANCE = new MessageSchema();

  static RocksDbSerializer<String> keySerializer = new KryoSerializer(String.class);
  static RocksDbSerializer<Message> valueSerializer = new KryoSerializer(Message.class);

  public static final RocksDbColumnFamily<String, Message> HOT_MESSAGE = RocksDbColumnFamily
      .builder()
      .cfId("cf_hot")
      .keySerializer(keySerializer)
      .valueSerializer(valueSerializer).build();
  public static final RocksDbColumnFamily<String, Message> ARCHIVES_MESSAGE = RocksDbColumnFamily
      .builder()
      .cfId("cf_archives")
      .keySerializer(keySerializer)
      .valueSerializer(valueSerializer).build();

  private static final List<RocksDbColumnFamily<?, ?>> ALL_SCHEMA = Arrays
      .asList(HOT_MESSAGE, ARCHIVES_MESSAGE);

  @Override
  public List<RocksDbColumnFamily<?, ?>> getAllColumns() {
    return ALL_SCHEMA;
  }
}
