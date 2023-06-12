package com.coolbeevip.hive;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;

import java.util.HashMap;
import java.util.Map;

public class HiveManagement {
  private static Map<String, HiveMetaStoreClient> hiveClients = new HashMap<>();
  private static Map<String, HiveConf> hiveConfs = new HashMap<>();

  public static void addHiveClient(String hiveName, String metastoreUri) throws MetaException {
    HiveConf conf = new HiveConf();
    conf.setVar(HiveConf.ConfVars.METASTOREURIS, metastoreUri);
    HiveMetaStoreClient client = new HiveMetaStoreClient(conf);
    hiveClients.put(hiveName, client);
    hiveConfs.put(hiveName, conf);
  }

  public static HiveMetaStoreClient getHiveClient(String hiveName) {
    return hiveClients.get(hiveName);
  }

  public static HiveConf getHiveConf(String hiveName) {
    return hiveConfs.get(hiveName);
  }
}
