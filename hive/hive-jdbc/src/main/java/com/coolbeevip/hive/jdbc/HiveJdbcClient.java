package com.coolbeevip.hive.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class HiveJdbcClient implements AutoCloseable {
  private final Connection conn;

  public HiveJdbcClient(HiveJdbcConfig config) throws ClassNotFoundException, SQLException {
    Class.forName(config.getJdbcDriverName());
    conn = DriverManager.getConnection(config.getJdbcUri(), config.getJdbcUsername(), config.getJdbcPassword());
  }

  public Statement createStatement() throws SQLException {
    return this.conn.createStatement();
  }

  public PreparedStatement createPrepareStatement(String sql) throws SQLException {
    return this.conn.prepareStatement(sql);
  }

  @Override
  public void close() throws Exception {
    if (conn != null) conn.close();
  }
}
