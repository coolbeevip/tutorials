package com.coolbeevip.hive.jdbc;

import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class HiveJdbcManagement implements AutoCloseable {
  private static Logger log = org.slf4j.LoggerFactory.getLogger(HiveJdbcManagement.class);
  private static Map<String, HiveJdbcClient> hiveClients = new HashMap<>();

  public HiveJdbcManagement() {

  }

  public void addHiveClient(HiveJdbcConfig config) throws SQLException, ClassNotFoundException {
    hiveClients.put(config.getName(), new HiveJdbcClient(config));
  }

  public HiveJdbcClient getHiveClient(String name) {
    return hiveClients.get(name);
  }

  @Override
  public void close() throws Exception {
    hiveClients.values().forEach(hiveClient -> {
      try {
        hiveClient.close();
      } catch (Exception e) {
        log.error("close hive client error", e);
      }
    });
    hiveClients.clear();
  }
}
