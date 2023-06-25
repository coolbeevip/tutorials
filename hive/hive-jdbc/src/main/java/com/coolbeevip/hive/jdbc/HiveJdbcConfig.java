package com.coolbeevip.hive.jdbc;

public class HiveJdbcConfig {
  private String name;

  private String jdbcDriverName = "org.apache.hive.jdbc.HiveDriver";
  private String jdbcUri;
  private String jdbcUsername;
  private String jdbcPassword;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getJdbcDriverName() {
    return jdbcDriverName;
  }

  public void setJdbcDriverName(String jdbcDriverName) {
    this.jdbcDriverName = jdbcDriverName;
  }

  public String getJdbcUri() {
    return jdbcUri;
  }

  public void setJdbcUri(String jdbcUri) {
    this.jdbcUri = jdbcUri;
  }

  public String getJdbcUsername() {
    return jdbcUsername;
  }

  public void setJdbcUsername(String jdbcUsername) {
    this.jdbcUsername = jdbcUsername;
  }

  public String getJdbcPassword() {
    return jdbcPassword;
  }

  public void setJdbcPassword(String jdbcPassword) {
    this.jdbcPassword = jdbcPassword;
  }
}
