package com.coolbeevip.shardingsphere.jpa;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
public class ShardingSphereConfiguration {

  @Bean
  DataSource dataSource() throws SQLException {
    // 配置真实数据源
    Map<String, DataSource> dataSourceMap = new HashMap<>();

    // 配置第 1 个数据源
    HikariDataSource dataSource1 = new HikariDataSource();
    dataSource1.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource1.setJdbcUrl("jdbc:mysql://localhost:3306/db0");
    dataSource1.setUsername("dbuser0");
    dataSource1.setPassword("dbpwd0");
    dataSourceMap.put("ds0", dataSource1);

    // 配置第 2 个数据源
    HikariDataSource dataSource2 = new HikariDataSource();
    dataSource2.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource2.setJdbcUrl("jdbc:mysql://localhost:3306/db1");
    dataSource2.setUsername("dbuser1");
    dataSource2.setPassword("dbpwd1");
    dataSourceMap.put("ds1", dataSource2);

    // 配置 T_ORDERS 表规则
    ShardingTableRuleConfiguration orderTableRuleConfig = new ShardingTableRuleConfiguration(
        "T_ORDERS", "ds${0..1}.T_ORDERS${0..1}");

    // 配置分库策略
    //orderTableRuleConfig.setDatabaseShardingStrategy(
    //    new StandardShardingStrategyConfiguration("user_id", "dbShardingAlgorithm"));

    // 配置分表策略
    orderTableRuleConfig.setTableShardingStrategy(
        new StandardShardingStrategyConfiguration("order_id", "tableShardingAlgorithm"));

    // 省略配置 t_order_item 表规则...

    // 配置分片规则
    ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
    shardingRuleConfig.getTables().add(orderTableRuleConfig);

    // 配置分库算法
    Properties dbShardingAlgorithmrProps = new Properties();
    dbShardingAlgorithmrProps.setProperty("algorithm-expression", "ds${user_id % 2}");
    shardingRuleConfig.getShardingAlgorithms().put("dbShardingAlgorithm",
        new ShardingSphereAlgorithmConfiguration("INLINE", dbShardingAlgorithmrProps));

    // 配置分表算法
    Properties tableShardingAlgorithmrProps = new Properties();
    tableShardingAlgorithmrProps.setProperty("algorithm-expression", "t_order${order_id % 2}");
    shardingRuleConfig.getShardingAlgorithms().put("tableShardingAlgorithm",
        new ShardingSphereAlgorithmConfiguration("INLINE", tableShardingAlgorithmrProps));

    // 创建 ShardingSphereDataSource
    return ShardingSphereDataSourceFactory
        .createDataSource(dataSourceMap, Collections.singleton(shardingRuleConfig),
            new Properties());
  }

}