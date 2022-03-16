package com.coolbeevip.shardingsphere.configuration;

import com.zaxxer.hikari.HikariDataSource;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfiguration {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  Map<String, DataSource> atomDataSourceMap = new HashMap<>();

  public DataSourceConfiguration() {
    HikariDataSource dataSource1 = new HikariDataSource();
    dataSource1.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource1.setJdbcUrl("jdbc:mysql://localhost:3306/db0");
//    dataSource1.setUsername("root");
//    dataSource1.setPassword("root");
    dataSource1.setUsername("dbuser0");
    dataSource1.setPassword("dbpwd0");
    atomDataSourceMap.put("ds0", dataSource1);

    HikariDataSource dataSource2 = new HikariDataSource();
    dataSource2.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource2.setJdbcUrl("jdbc:mysql://localhost:3306/db1");
//    dataSource2.setUsername("root");
//    dataSource2.setPassword("root");
    dataSource2.setUsername("dbuser1");
    dataSource2.setPassword("dbpwd1");
    atomDataSourceMap.put("ds1", dataSource2);

    atomDataSourceMap.forEach((k, v) -> {
      log.info("Flyway init datasource {}",k);
      FlywayConfiguration flywayConfiguration = new FlywayConfiguration(v, "classpath:/db/mysql/"+k);
      flywayConfiguration.migrate();
    });
  }

  @Bean
  DataSource shardingDataSource() throws SQLException {
    DataSource shardingDataSource = ShardingSphereFactory.createDataSource(atomDataSourceMap);
    return shardingDataSource;
  }

//  @Bean
//  DataSource dataSource() throws SQLException {
//    return atomDataSourceMap.get("ds0");
//  }

}