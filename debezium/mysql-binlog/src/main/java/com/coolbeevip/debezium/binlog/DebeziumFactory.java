package com.coolbeevip.debezium.binlog;

import io.debezium.config.Configuration;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebeziumFactory {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public Configuration configuration(String dbHost, int dbPort, String dbUsername,
      String dbPassword, String dbName) {
    log.info("dbHost {}, dbPort {}, dbUsername {}, dbName {}", dbHost, dbPort, dbUsername, dbName);
    return Configuration.create()
        .with("name", "customer-mysql-connector")
        .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
        .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
        .with("offset.storage.file.filename", "logs/offsets.dat")
        .with("offset.flush.interval.ms", "60000")
        .with("database.hostname", dbHost)
        .with("database.port", dbPort)
        .with("database.user", dbUsername)
        .with("database.password", dbPassword)
        .with("database.dbname", dbName)
        .with("database.include.list", dbName)
        .with("include.schema.changes", "false")
        .with("database.server.id", "10181")
        .with("database.server.name", "customer-mysql-db-server")
        .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
        .with("database.history.file.filename", "logs/dbhistory.dat")
        .build();
  }
}