package com.coolbeevip.hive;


import org.apache.hadoop.hive.metastore.ColumnType;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.metastore.api.*;
import org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hive.hcatalog.streaming.*;
import org.apache.thrift.TException;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class HiveManagementTest {

  private static final String HIVE_NAME = "myHive";
  private static final String HIVE_DATABASE = "testdb";
  private static final String HIVE_TABLE = "table_students";
  private static final String LOCATION = "/ZL_TEST";
  // private static final String URI = "thrift://10.19.36.211:9083";
  private static final String METASTORE_URI = "thrift://10.19.83.185:9083";
  private static final String JDBC_URI = "jdbc:hive2://10.19.83.185:10000";

  @Test
  public void createTableTest() throws TException {
    HiveMetaStoreClient client = HiveManagement.getHiveClient(HIVE_NAME);
    Table existTable = null;
    try {
      existTable = client.getTable(HIVE_DATABASE, HIVE_TABLE);
    } catch (TException e) {
      if (e instanceof NoSuchObjectException) {
        // ignore
      } else {
        throw new RuntimeException(e);
      }
    }
    if (existTable == null) {
      client.createTable(getTableDesc(false));
      assertThat(client.getTable(HIVE_DATABASE, HIVE_TABLE), is(notNullValue()));

      // drop
      client.dropTable(HIVE_DATABASE, HIVE_TABLE);

      try {
        client.getTable(HIVE_DATABASE, HIVE_TABLE);
      } catch (TException e) {
        assertThat(e, Matchers.instanceOf(NoSuchObjectException.class));
      }
    }
  }

  @Test
  public void insertTransactionTableTest() throws TException {
    HiveMetaStoreClient client = HiveManagement.getHiveClient(HIVE_NAME);
    client.createTable(getTableDesc(true));

    int txnsPerBatch = 10;
    String delimiter = ",";
    String[] fieldToColMapping = new String[]{"id", "name", "age", "country"};
    Character serdeSeparator = null;
    try {
      HiveEndPoint hiveEndPoint = new HiveEndPoint(METASTORE_URI, HIVE_DATABASE, HIVE_TABLE, null);
      StreamingConnection connection = hiveEndPoint.newConnection(true);
      RecordWriter recordWriter = new DelimitedInputWriter(fieldToColMapping, delimiter, hiveEndPoint, null, serdeSeparator);

      try {
        TransactionBatch transactionBatch = connection.fetchTransactionBatch(txnsPerBatch, recordWriter);

        transactionBatch.write("1,val1,Asia,China".getBytes());
        transactionBatch.write("2,val2,Asia,India".getBytes());
        transactionBatch.write("3,val3,Europe,Germany".getBytes());
        transactionBatch.write("4,val4,Europe,France".getBytes());
        transactionBatch.write("5,val5,America,USA".getBytes());
        transactionBatch.write("6,val6,America,Canada".getBytes());
        transactionBatch.write("7,val7,Africa,Egypt".getBytes());
        transactionBatch.write("8,val8,Africa,South Africa".getBytes());
        transactionBatch.write("9,val9,Australia,Australia".getBytes());
        transactionBatch.write("10,val10,Australia,New Zealand".getBytes());

        transactionBatch.commit();
      } finally {
        connection.close();
      }


    } catch (ImpersonationFailed e) {
      throw new RuntimeException(e);
    } catch (PartitionCreationFailed e) {
      throw new RuntimeException(e);
    } catch (InvalidTable e) {
      throw new RuntimeException(e);
    } catch (ConnectionError e) {
      throw new RuntimeException(e);
    } catch (InvalidPartition e) {
      throw new RuntimeException(e);
    } catch (StreamingException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } finally {
      client.dropTable(HIVE_DATABASE, HIVE_TABLE);
    }
  }

  @BeforeClass
  public static void setup() throws TException {
    HiveManagement.addHiveClient(HIVE_NAME, METASTORE_URI);
    HiveMetaStoreClient client = HiveManagement.getHiveClient(HIVE_NAME);
    Database database = null;
    try {
      database = client.getDatabase(HIVE_DATABASE);
    } catch (TException e) {
      if (e instanceof NoSuchObjectException) {
        // ignore
      } else {
        throw e;
      }
    }
    if (database == null) {
      client.createDatabase(new Database(HIVE_DATABASE, "test database", LOCATION, null));
    }
  }

  @AfterClass
  public static void tearDown() throws TException {
    HiveManagement.addHiveClient(HIVE_NAME, METASTORE_URI);
    HiveMetaStoreClient client = HiveManagement.getHiveClient(HIVE_NAME);
    Database database = client.getDatabase(HIVE_DATABASE);
    if (database != null) {
      client.dropDatabase(HIVE_DATABASE, true, true, true);
    }
  }

  /*
   * Hive 需要开启实物表支持
   * set hive.txn.manager=org.apache.hadoop.hive.ql.lockmgr.DbTxnManager;
   * set hive.support.concurrency=true;
   * */
  private Table getTableDesc(boolean acid) {
    // Define the table schema
    List<FieldSchema> cols = new ArrayList<>();
    cols.add(new FieldSchema("id", "int", "id column"));
    cols.add(new FieldSchema("name", "string", "name column"));
    cols.add(new FieldSchema("age", "int", "age column"));

    ArrayList<FieldSchema> partitionCols = new ArrayList<>(1);
    partitionCols.add(new FieldSchema("country", ColumnType.STRING_TYPE_NAME, ""));

    // Define the table details
    StorageDescriptor sd = new StorageDescriptor();
    sd.setLocation(LOCATION + "/" + HIVE_TABLE);
    sd.setCols(cols);
    if (acid) {
      sd.setInputFormat(org.apache.hadoop.hive.ql.io.orc.OrcInputFormat.class.getName());
      sd.setOutputFormat(org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat.class.getName());
    } else {
      sd.setInputFormat(TextInputFormat.class.getName());
      sd.setOutputFormat(HiveIgnoreKeyTextOutputFormat.class.getName());
    }
    SerDeInfo serDeInfo = new SerDeInfo();
    serDeInfo.setParameters(new HashMap<>());
    if (acid) {
      serDeInfo.setSerializationLib("org.apache.hadoop.hive.ql.io.orc.OrcSerde");
    } else {
      serDeInfo.setSerializationLib("org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe");
      serDeInfo.getParameters().put("field.delim", ",");
    }
    sd.setSerdeInfo(serDeInfo);

    sd.setSkewedInfo(new SkewedInfo());
    sd.setCompressed(false);
    sd.setNumBuckets(-1);

    Table tbl = new Table();
    tbl.setTableType(TableType.EXTERNAL_TABLE.name());
    tbl.setDbName(HIVE_DATABASE);
    tbl.setTableName(HIVE_TABLE);
    tbl.setOwner("ndcp");
    tbl.setPartitionKeys(partitionCols);
    tbl.setSd(sd);

    tbl.setParameters(new LinkedHashMap<>());
    if (acid) {
      tbl.getParameters().put("transactional", "true");
      tbl.getParameters().put("orc.compress", "ZLIB");
      tbl.getParameters().put("transaction.manager", "org.apache.hadoop.hive.ql.lockmgr.DbTxnManager");
      tbl.getParameters().put("transaction.provider", "org.apache.hadoop.hive.ql.lockmgr.DbTxnManager");
      tbl.getParameters().put("immutable", "true");
      tbl.getParameters().put("orc.compress", "ZLIB");
    }
    return tbl;
  }
}
