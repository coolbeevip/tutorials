package com.coolbeevip.iotdb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.coolbeevip.iotdb.core.MeasurementValue;
import com.coolbeevip.iotdb.enties.CardDevice;
import java.io.IOException;
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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

public class CardDeviceTest {

  private String storage_group = "root.zhanglei";
  private static Session session;
  private static int IOTDB_RPC_PORT = 6667;
  protected static GenericContainer iotdb;

  @BeforeClass
  @SneakyThrows
  public static void setup() throws IOException {
    //docker run --rm --name some-iotdb -p 6667:6667 -p 31999:31999 -p 8181:8181 apache/iotdb:0.12.5
    // apache/iotdb:0.12.6-node
    iotdb = new GenericContainer(DockerImageName.parse("apache/iotdb:0.12.5"));
    iotdb.withExposedPorts(IOTDB_RPC_PORT)
        .withAccessToHost(true)
        .waitingFor(new LogMessageWaitStrategy().withRegEx(".*IoTDB has started.*"))
        .start();
    session = new Session.Builder()
        .host("127.0.0.1")
        .port(iotdb.getFirstMappedPort())
        .username("root")
        .password("root")
        .build();
    session.open();
  }

  @AfterClass
  @SneakyThrows
  public static void tearDown() {
    if (session != null) {
      session.close();
    }
    iotdb.stop();
  }

  @Test
  @SneakyThrows
  public void createTimeSeriesTest() {
    String path = "root.test.city.site1.VENDORS1.ELEMENT1.temperature";
    session.createTimeseries(path, TSDataType.DOUBLE, TSEncoding.GORILLA, CompressionType.SNAPPY);
    assertThat(true, is(session.checkTimeseriesExists(path)));
    session.deleteTimeseries(path);
    assertThat(false, is(session.checkTimeseriesExists(path)));
  }

  @Test
  @SneakyThrows
  public void testDevice() {
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
    card.batchInsert("temp", measurementValues, 1000);
  }
}