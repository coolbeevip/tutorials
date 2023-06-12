package com.coolbeevip.hive;


import org.hamcrest.Matchers;
import org.junit.*;

import java.sql.*;

import static org.hamcrest.MatcherAssert.assertThat;

public class HiveJdbcTest {
  private static Connection conn;
  private static final String HIVE_NAME = "myHive";
  private static final String HIVE_DATABASE = "";
  private static final String HIVE_TABLE = "table_students";
  private static final String JDBC_URI = "jdbc:hive2://10.19.83.185:10000/";

  @Test
  public void insertTableTest() throws SQLException {
    String insertSql = insertTableSql(HIVE_TABLE);
    try (PreparedStatement pStmt = conn.prepareStatement(insertSql)) {
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
   * */
  @Test
  public void loadDataTable() throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      stmt.execute(loadDataSql("/opt/hive/data/warehouse/import/data.csv", HIVE_TABLE));

      ResultSet rs = stmt.executeQuery("select count(1) c1 from " + HIVE_TABLE);
      while (rs.next()) {
        Integer c1 = rs.getInt("c1");
        assertThat(c1, Matchers.is(3));
      }
    }
  }

  @Before
  public void setup() throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      stmt.execute(createTableSql(HIVE_TABLE));
    }
  }

  @After
  public void tearDown() throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      stmt.execute(dropTableSql(HIVE_TABLE));
    }
  }

  @BeforeClass
  public static void setupAll() throws SQLException, ClassNotFoundException {
    Class.forName("org.apache.hive.jdbc.HiveDriver");
    conn = DriverManager.getConnection(JDBC_URI, "", "");
  }

  @AfterClass
  public static void tearDownAll() throws SQLException {
    if (conn != null) conn.close();
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
        .append("create external table if not exists " + tableName + "(id int, name string, age int)\n")
        .append("partitioned by (country string)\n")
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
