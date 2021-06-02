package com.coolbeevip.rocksdb.core;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zhanglei
 */
public interface Schema {

  byte[] DEFAULT_COLUMN_ID = "default".getBytes(StandardCharsets.UTF_8);

  List<RocksDbVariable<?, ?>> getAllColumns();
}
