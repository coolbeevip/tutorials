package com.coolbeevip.hive.jdbc;


import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.MatcherAssert.assertThat;

public class HiveJdbcManagementTest {

  private static HiveJdbcManagement hiveJdbcManagement;
  private static String DS_NAME = "my_hive";
  private static final String HIVE_TABLE = "table_students";
  private static final String HIVE_PRINCIPAL = "";
  private static final String HIVE_LOGIN_CONF = "gss-jaas.conf";
  private static final String HIVE_KRB5_CONF = "krb5.conf";
  private static final String JDBC_URI = "jdbc:hive2://10.19.36.44:10000/default;principal=" + HIVE_PRINCIPAL;

  @Test
  public void insertTableTest() throws Exception {
    HiveJdbcClient jdbcClient = hiveJdbcManagement.getHiveClient(DS_NAME);
    String insertSql = insertTableSql(HIVE_TABLE);
    try (PreparedStatement pStmt = jdbcClient.createPrepareStatement(insertSql)) {
      for (int i = 0; i < 10; i++) {
        pStmt.setInt(1, i);
        pStmt.setString(2, "张" + i);
        pStmt.setInt(3, i);
        pStmt.setString(4, "CHINA");
        pStmt.execute();
      }
      ResultSet rs = pStmt.executeQuery("select count(1) c1 from " + HIVE_TABLE);
      while (rs.next()) {
        Integer c1 = rs.getInt("c1");
        assertThat(c1, Matchers.is(10));
      }
    }
  }


  /**
   * $ cat data.csv
   * 1,A,30,CHINA
   * 2,B,40,CHINA
   * 3,TOM,25,UK
   */
  /*
  @Test
  public void loadDataTable() throws Exception {
    HiveJdbcClient jdbcClient = hiveJdbcManagement.getHiveClient(DS_NAME);
    try (Statement stmt = jdbcClient.createStatement()) {
      stmt.execute(loadDataSql("/opt/hive/data/warehouse/import/data.csv", HIVE_TABLE));
      ResultSet rs = stmt.executeQuery("select count(1) c1 from " + HIVE_TABLE);
      while (rs.next()) {
        Integer c1 = rs.getInt("c1");
        assertThat(c1, Matchers.is(3));
      }
    }
  }*/
  @Before
  public void setup() throws Exception {
    HiveJdbcClient jdbcClient = hiveJdbcManagement.getHiveClient(DS_NAME);
    try (Statement stmt = jdbcClient.createStatement()) {
      stmt.execute(createTableSql(HIVE_TABLE));
    }

  }

  @After
  public void tearDown() throws Exception {
    HiveJdbcClient jdbcClient = hiveJdbcManagement.getHiveClient(DS_NAME);
    try (Statement stmt = jdbcClient.createStatement()) {
      stmt.execute(dropTableSql(HIVE_TABLE));
    }
  }

  @BeforeClass
  public static void setupAll() throws SQLException, ClassNotFoundException {

    System.setProperty("java.security.auth.login.config", HIVE_LOGIN_CONF);
    System.setProperty("sun.security.jgss.debug", "true");
    System.setProperty("sun.security.krb5.debug", "true");
    System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
    System.setProperty("java.security.krb5.conf", HIVE_KRB5_CONF);

    hiveJdbcManagement = new HiveJdbcManagement();
    HiveJdbcConfig config = new HiveJdbcConfig();
    config.setName(DS_NAME);
    config.setJdbcDriverName("org.apache.hive.jdbc.HiveDriver");
    config.setJdbcUri(JDBC_URI);
    config.setJdbcUsername(null);
    config.setJdbcPassword(null);
    hiveJdbcManagement.addHiveClient(config);
  }

  @AfterClass
  public static void tearDownAll() throws Exception {
    if (hiveJdbcManagement != null) hiveJdbcManagement.close();
  }

  private String loadDataSql(String path, String tableName) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("load data local inpath '")
        .append(path)
        .append("' into table ")
        .append(tableName);
    return buffer.toString();
  }

  private String insertTableSql(String tableName) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("insert into " + tableName + "(id, name, age, country) values (?, ?, ?, ?)");
    return buffer.toString();
  }

  private String createTableSql(String tableName) {
    StringBuffer buffer = new StringBuffer();
    buffer
        .append("create external table if not exists " + tableName + "(id int, name string, age int, country string)\n")
        .append("row format delimited\n")
        .append("fields terminated by ','\n")
        .append("stored as textfile\n")
        .append("location '/opt/hive/data/warehouse/hive_example'");
    return buffer.toString();
  }

  private String dropTableSql(String tableName) {
    return "drop table if exists " + tableName;
  }
}
