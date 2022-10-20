package com.coolbeevip.shardingsphere.configuration;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayConfiguration {

  private final Flyway flyway;

  public FlywayConfiguration(DataSource dataSource,String location) {
    this.flyway = Flyway.configure()
        .locations(location)
        .baselineOnMigrate(true)
        .group(true)
        .dataSource(dataSource)
        .load();
  }

  public void migrate(){
    flyway.migrate();
  }
}