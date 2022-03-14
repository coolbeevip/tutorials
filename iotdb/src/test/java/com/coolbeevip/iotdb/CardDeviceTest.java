package com.coolbeevip.iotdb;

import com.coolbeevip.iotdb.core.MeasurementValue;
import com.coolbeevip.iotdb.enties.CardDevice;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CardDeviceTest {

  private String storage_group = "root.zhanglei";
  private static Session session;

  @Test
  @SneakyThrows
  public void createTimeSeriesTest(){
    String path1 = "root.test.NULL.SITE1.VENDORS1.ELEMENT1.temperature";
    //session.createTimeseries(path1, TSDataType.DOUBLE, TSEncoding.GORILLA, CompressionType.SNAPPY);
    //session.deleteTimeseries(path1);

//    String path2 = "root.test.SITE1.VENDORS1.NULL.ELEMENT1.temperature";
//    session.createTimeseries(path2, TSDataType.DOUBLE, TSEncoding.GORILLA, CompressionType.SNAPPY);
//    session.deleteTimeseries(path2);

    try{
      if(!session.checkTimeseriesExists(path1)){

      }
//      session.createTimeseries(path, TSDataType.DOUBLE, TSEncoding.GORILLA, CompressionType.SNAPPY);
    }finally {
      //session.deleteTimeseries(path);
    }
  }

  @Test
  @SneakyThrows
  public void testDevice(){
    CardDevice card = CardDevice.builder()
        .session(session)
        .storageGroup(storage_group)
        .addPath("SITE1")
        .addPath("ELEMENT2")
        .addMeasurement("temp", TSDataType.DOUBLE, TSEncoding.GORILLA,
            CompressionType.SNAPPY, null, null, null, "温度").build();
    card.initStorageGroup();
    card.initTimeSeries();

    int total = 10000000;
    long timestamp = System.currentTimeMillis();
    List<MeasurementValue> measurementValues = new ArrayList<>();
    for (int row = 0; row < total; row++) {
      measurementValues.add(new MeasurementValue(timestamp, Math.random()));
      timestamp++;
    }
    card.batchInsert("temp",measurementValues,1000);
  }

  @BeforeClass
  @SneakyThrows
  public static void setupClass() {
    session = new Session.Builder()
        .host("127.0.0.1")
        .port(6667)
        .username("root")
        .password("root")
        .build();
    session.open();
  }

  @AfterClass
  @SneakyThrows
  public static void tearDownClass() {
    if (session != null) {
      session.close();
    }
  }


}