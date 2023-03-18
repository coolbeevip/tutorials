package com.coolbeevip.calcite.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.calcite.adapter.elasticsearch.ElasticsearchSchema;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class Main {
  public static void main(String[] args) throws Exception {
    RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
    // 指定索引库
    ElasticsearchSchema elasticsearchSchema = new ElasticsearchSchema(restClient, new ObjectMapper(), null);

    // 2.构建Connection
    // 2.1 设置连接参数
    Properties info = new Properties();
    // 不区分sql大小写
    info.setProperty("caseSensitive", "false");

    // 2.2 获取标准的JDBC Connection
    Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
    // 2.3 获取Calcite封装的Connection
    CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);

    // 3.构建RootSchema，在Calcite中，RootSchema是所有数据源schema的parent，多个不同数据源schema可以挂在同一个RootSchema下
    // 以实现查询不同数据源的目的
    SchemaPlus rootSchema = calciteConnection.getRootSchema();

    // 4.将不同数据源schema挂载到RootSchema，这里添加ElasticsearchSchema
    rootSchema.add("es", elasticsearchSchema);

    // 5.执行SQL查询，通过SQL方式访问object对象实例
    // 条件查询
    // String sql = "SELECT _MAP['id'],_MAP['title'],_MAP['price'] FROM es.books WHERE _MAP['price'] > 60 LIMIT 2";
    // 统计索引数量
    // String sql = "SELECT count(*) FROM es.books WHERE _MAP['price'] > 50 ";
    // 分页查询
    String sql = "SELECT * FROM es.ndcp_metrics_collector_status_202303 WHERE _MAP['header.type'] = 'HEARTBEAT' offset 0 fetch next 3 rows only";
    Statement statement = calciteConnection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);

    // 6.遍历打印查询结果集
    System.out.println(ResultSetUtil.resultString(resultSet));
  }
}
