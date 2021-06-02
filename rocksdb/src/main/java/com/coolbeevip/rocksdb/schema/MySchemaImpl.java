package com.coolbeevip.rocksdb.schema;


import com.coolbeevip.rocksdb.core.RocksDbVariable;
import com.coolbeevip.rocksdb.serialization.KryoSerializer;
import com.coolbeevip.rocksdb.serialization.RocksDbSerializer;
import java.util.Arrays;
import java.util.List;

public class MySchemaImpl implements MySchema {

  public static final MySchemaImpl INSTANCE = new MySchemaImpl();

  static RocksDbSerializer<String> keySerializer = new KryoSerializer(String.class);
  static RocksDbSerializer<RecordRow> valueSerializer = new KryoSerializer(RecordRow.class);


  public static final RocksDbVariable<String, RecordRow> HOT_MESSAGE = RocksDbVariable.builder()
      .cfId("cf_hot")
      .keySerializer(keySerializer)
      .valueSerializer(valueSerializer).build();

  public static final RocksDbVariable<String, RecordRow> ARCHIVES_MESSAGE = RocksDbVariable
      .builder()
      .cfId("cf_archives")
      .keySerializer(keySerializer)
      .valueSerializer(valueSerializer).build();

  private static final List<RocksDbVariable<?, ?>> ALL_COLUMNS = Arrays
      .asList(HOT_MESSAGE, ARCHIVES_MESSAGE);

  @Override
  public List<RocksDbVariable<?, ?>> getAllColumns() {
    return ALL_COLUMNS;
  }
}
