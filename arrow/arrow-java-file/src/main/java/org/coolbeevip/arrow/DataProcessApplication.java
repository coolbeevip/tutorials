package org.coolbeevip.arrow;

import io.netty.util.internal.ThreadLocalRandom;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import okio.ByteString;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.complex.StructVector;
import org.apache.arrow.vector.ipc.ArrowFileReader;
import org.apache.arrow.vector.ipc.SeekableReadChannel;
import org.apache.arrow.vector.types.pojo.Schema;
import org.coolbeevip.arrow.labs.Address;
import org.coolbeevip.arrow.labs.ChunkedWriter;
import org.coolbeevip.arrow.labs.Person;
import org.coolbeevip.arrow.labs.PersonVectorized;
import org.coolbeevip.arrow.labs.SchemaRepositories;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataProcessApplication {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static SchemaRepositories schemaRepositories = new SchemaRepositories();

  private static final String[] FIRST_NAMES = new String[]{
      "John", "Jane", "Gerard", "Aubrey", "Amelia"};
  private static final String[] LAST_NAMES = new String[]{"Smith", "Parker", "Phillips", "Jones"};
  private static final String[] STREETS = new String[]{
      "Halloway", "Sunset Boulvard", "Wall Street", "Secret Passageway"};
  private static final String[] CITIES = new String[]{
      "Brussels", "Paris", "London", "Amsterdam"};


  public static void main(String[] args) throws Exception {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    schemaRepositories.load(Address.class);
    schemaRepositories.load(Person.class);
    DataProcessApplication app = new DataProcessApplication();
    app.generateRandomData();
    app.filterSingleColumn();
    app.filterMultiColumn();
    stopWatch.stop();
    log.info("Timing: {}", stopWatch.getTime());
  }

  /**
   * 生成随机数据
   */
  private void generateRandomData() throws IOException {
    int numberOfPeople = 1_000_000;
    int chunkSize = 10_000;

    Person[] people = new Person[numberOfPeople];
    for (int i = 0; i < numberOfPeople; i++) {
      people[i] = randomPerson();
    }
    Schema personSchema = schemaRepositories.getSchema(Person.class);
    PersonVectorized personVectorized = new PersonVectorized(schemaRepositories);
    ChunkedWriter<Person> writer = new ChunkedWriter<>(personSchema, chunkSize,
        personVectorized::vectorized);
    writer.write(new File("people.arrow"), people);
  }

  /**
   * 过滤单列测试
   * - 过滤住址名称以 'way' 结尾的数据
   * - 按照 'city' 分组
   * - 汇聚平均年龄
   */
  public void filterSingleColumn() throws IOException {
    RootAllocator allocator = new RootAllocator();

    try (FileInputStream fd = new FileInputStream("people.arrow")) {
      // 装载文件
      ArrowFileReader fileReader = new ArrowFileReader(new SeekableReadChannel(fd.getChannel()),
          allocator);
      fileReader.initialize();
      VectorSchemaRoot schemaRoot = fileReader.getVectorSchemaRoot();

      // 汇聚: 使用 ByteString 更快
      Map<ByteString, Long> perCityCount = new TreeMap<>();
      Map<ByteString, Long> perCitySum = new TreeMap<>();

      while (fileReader.loadNextBatch()) {

        // 获取符合街道后缀的数据索引
        StructVector addressVector = (StructVector) schemaRoot.getVector("address");
        VarCharVector streetVector = (VarCharVector) addressVector.getChild("street");
        List<Integer> streetSelectedIndexes = new ArrayList();
        byte[] suffixInBytes = "way".getBytes();
        for (int i = 0; i < schemaRoot.getRowCount(); i++) {
          if (ByteString.of(streetVector.get(i)).endsWith(suffixInBytes)) {
            streetSelectedIndexes.add(i);
          }
        }

        // 统计城市人口和年龄合计
        VarCharVector cityVector = (VarCharVector) ((StructVector) schemaRoot.getVector("address"))
            .getChild("city");
        IntVector ageDataVector = (IntVector) schemaRoot.getVector("age");

        for (int selectedIndex : streetSelectedIndexes) {
          ByteString city = ByteString.of(cityVector.get(selectedIndex));
          // 存储城市人口数
          perCityCount.put(city, perCityCount.getOrDefault(city, 0L) + 1);
          // 存储城市人口年龄合计
          perCitySum
              .put(city, perCitySum.getOrDefault(city, 0L) + ageDataVector.get(selectedIndex));
        }
      }

      // 通过城市人口和年龄和，计算城市年龄平均值
      for (ByteString city : perCityCount.keySet()) {
        double average = (double) perCitySum.get(city) / perCityCount.get(city);
        log.info("City = {}; Average = {}", city, average);
      }
    }
  }

  /**
   * 过滤多列测试
   * - 过滤 'last name' 以 'P' 开头
   * - 过滤年龄在 18 至 35 之间
   * - 过滤居住地址以 'way' 结尾
   * - 按照 'city' 分组
   * - 汇聚平均年龄
   */
  public void filterMultiColumn() throws IOException {
    RootAllocator allocator = new RootAllocator();

    try (FileInputStream fd = new FileInputStream("people.arrow")) {
      // 装载数据文件
      ArrowFileReader fileReader = new ArrowFileReader(new SeekableReadChannel(fd.getChannel()),
          allocator);
      fileReader.initialize();
      VectorSchemaRoot schemaRoot = fileReader.getVectorSchemaRoot();

      // 汇聚: 存储城市人数和年龄和
      Map<ByteString, Long> perCityCount = new HashMap<>();
      Map<ByteString, Long> perCitySum = new HashMap<>();

      while (fileReader.loadNextBatch()) {

        // 过滤 lastName
        VarCharVector lastName = (VarCharVector) schemaRoot.getVector("lastName");
        List<Integer> lastNameSelectedIndexes = new ArrayList();
        byte[] prefixBytes = "P".getBytes();
        for (int i = 0; i < schemaRoot.getRowCount(); i++) {
          if (ByteString.of(lastName.get(i)).startsWith(prefixBytes)) {
            lastNameSelectedIndexes.add(i);
          }
        }

        // 过滤年龄
        IntVector age = (IntVector) schemaRoot.getVector("age");
        List<Integer> ageSelectedIndexes = new ArrayList();
        for (int i = 0; i < schemaRoot.getRowCount(); i++) {
          int currentAge = age.get(i);
          if (18 <= currentAge && currentAge <= 35) {
            ageSelectedIndexes.add(i);
          }
        }

        // 过滤住址
        StructVector addressVector = (StructVector) schemaRoot.getVector("address");
        VarCharVector streetVector = (VarCharVector) addressVector.getChild("street");
        List<Integer> streetSelectedIndexes = new ArrayList();
        byte[] suffixBytes = "way".getBytes();
        for (int i = 0; i < schemaRoot.getRowCount(); i++) {
          if (ByteString.of(streetVector.get(i)).endsWith(suffixBytes)) {
            streetSelectedIndexes.add(i);
          }
        }

        // 获取索引交集
        List<Integer> selectedIndexes = intersection(lastNameSelectedIndexes, ageSelectedIndexes);
        selectedIndexes = intersection(selectedIndexes, streetSelectedIndexes);

        // 存储符合条件的数据
        VarCharVector cityVector = (VarCharVector) ((StructVector) schemaRoot.getVector("address"))
            .getChild("city");
        IntVector ageDataVector = (IntVector) schemaRoot.getVector("age");
        for (int selectedIndex : selectedIndexes) {
          ByteString city = ByteString.of(cityVector.get(selectedIndex));
          perCityCount.put(city, perCityCount.getOrDefault(city, 0L) + 1);
          perCitySum
              .put(city, perCitySum.getOrDefault(city, 0L) + ageDataVector.get(selectedIndex));
        }
      }

      // 通过城市人口和年龄和，计算城市年龄平均值
      for (ByteString city : perCityCount.keySet()) {
        double average = (double) perCitySum.get(city) / perCityCount.get(city);
        log.info("City = {}; Average = {}", city, average);
      }
    }
  }

  private Address randomAddress() {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    return new Address(
        pickRandom(STREETS),
        random.nextInt(1, 3000),
        pickRandom(CITIES),
        random.nextInt(1000, 10000)
    );
  }

  private Person randomPerson() {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    return new Person(
        pickRandom(FIRST_NAMES),
        pickRandom(LAST_NAMES),
        random.nextInt(0, 100),
        randomAddress()
    );
  }

  private <T> T pickRandom(T[] options) {
    return options[ThreadLocalRandom.current().nextInt(0, options.length)];
  }

  private List<Integer> intersection(List<Integer> x, List<Integer> y) {
    int indexX = 0;
    int indexY = 0;
    List<Integer> intersection = new ArrayList<>();

    while (indexX < x.size() && indexY < y.size()) {
      if (x.get(indexX) < y.get(indexY)) {
        indexX++;
      } else if (x.get(indexX) > y.get(indexY)) {
        indexY++;
      } else {
        intersection.add(x.get(indexX));
        indexX++;
        indexY++;
      }
    }
    return intersection;
  }
}