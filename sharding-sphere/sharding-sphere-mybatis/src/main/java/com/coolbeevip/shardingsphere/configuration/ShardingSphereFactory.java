package com.coolbeevip.shardingsphere.configuration;

import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class ShardingSphereFactory {

  public static DataSource createDataSource(Map<String, DataSource> dataSourceMap)
      throws SQLException {

    // 配置 t_orders 表规则
    ShardingTableRuleConfiguration orderTableRuleConfig = new ShardingTableRuleConfiguration(
        "t_orders", "ds${0..1}.t_orders_${0..1}");

    /**
     * 配置分库策略 & 算法
     */
    orderTableRuleConfig.setDatabaseShardingStrategy(
        new StandardShardingStrategyConfiguration("customer_id", "dbShardingAlgorithm"));
    Properties dbShardingAlgorithmProps = new Properties();
    dbShardingAlgorithmProps
        .setProperty("algorithm-expression", "ds${Math.abs(customer_id.hashCode()) % 2}");

    /**
     * 配置分表策略 & 算法
     */
    orderTableRuleConfig.setTableShardingStrategy(
        new StandardShardingStrategyConfiguration("id", "tableShardingAlgorithm"));
    Properties tableShardingAlgorithmProps = new Properties();
    tableShardingAlgorithmProps
        .setProperty("algorithm-expression", "t_orders_${Math.abs(id.hashCode()) % 2}");

    /**
     * 配置分片规则
     */
    ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
    shardingRuleConfig.getTables().add(orderTableRuleConfig);
    shardingRuleConfig.getShardingAlgorithms().put("dbShardingAlgorithm",
        new ShardingSphereAlgorithmConfiguration("INLINE", dbShardingAlgorithmProps));
    shardingRuleConfig.getShardingAlgorithms().put("tableShardingAlgorithm",
        new ShardingSphereAlgorithmConfiguration("INLINE", tableShardingAlgorithmProps));
    // 配置广播表
    shardingRuleConfig.getBroadcastTables().add("t_customers");

    /**
     * 数据源
     */
    Properties properties = new Properties();
    properties.setProperty("sql-show", "true");
    return ShardingSphereDataSourceFactory
        .createDataSource(dataSourceMap, Collections.singleton(shardingRuleConfig), properties);
  }

}