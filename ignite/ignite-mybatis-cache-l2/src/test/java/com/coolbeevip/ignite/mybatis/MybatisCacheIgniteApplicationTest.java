package com.coolbeevip.ignite.mybatis;

import com.coolbeevip.ignite.mybatis.entities.AddressDO;
import com.coolbeevip.ignite.mybatis.entities.CountryDO;
import com.coolbeevip.ignite.mybatis.repository.AddressRepository;
import com.coolbeevip.ignite.mybatis.repository.CountryRepository;
import com.github.javafaker.Address;
import com.github.javafaker.Country;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@SpringBootTest(classes = {MybatisCacheIgniteApplication.class,
    MybatisCacheIgniteConfiguration.class})
public class MybatisCacheIgniteApplicationTest {

  static Faker faker = new Faker(new Locale("zh-CN"));
  static MySQLContainer mssql;

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.username", () -> {
      return mssql.getUsername();
    });
    registry.add("spring.datasource.password", () -> {
      return mssql.getPassword();
    });
    registry.add("spring.datasource.url", () -> {
      return mssql.getJdbcUrl();
    });
  }

  @Autowired
  AddressRepository addressRepository;

  @Autowired
  CountryRepository countryRepository;

  @Test
  public void initCountryTest() {
    for (int i = 0; i < 50; i++) {
      Country country = faker.country();
      CountryDO c = new CountryDO();
      c.setUuid(UUID.randomUUID().toString());
      c.setName(country.name());
      countryRepository.insertCountry(c);
    }
  }

  @Test
  public void initAddressTest() {
    int total = 100000;
    int batchSize = 5000;
    List<AddressDO> addresses = new ArrayList<>();
    while (total > 0) {
      if (addresses.size() < batchSize) {
        Address addr = faker.address();
        AddressDO address = new AddressDO();
        address.setUuid(UUID.randomUUID().toString());
        address.setRemark("Faker");
        address.setName(addr.fullAddress());
        address.setLatitude(Double.valueOf(addr.latitude()));
        address.setLongitude(Double.valueOf(addr.longitude()));
        address.setPostcode(addr.zipCode());
        address.setCountry(addr.country());
        address.setCity(addr.city());
        addresses.add(address);
        total--;
      } else {
        long begin = System.currentTimeMillis();
        addressRepository.insertAddresses(addresses);
        addresses.clear();
        long end = System.currentTimeMillis();
        log.info("batch {} save {}ms", batchSize, end - begin);
      }
    }
    if (addresses.size() > 0) {
      addressRepository.insertAddresses(addresses);
    }
  }

  @Test
  public void test() {
    new Timer() {
      @Override
      long mapper() {
        return addressRepository.getAllAddress().size();
      }
    }.exec("首次全量读取");

    new Timer() {
      @Override
      long mapper() {
        return addressRepository.getAllAddress().size();
      }
    }.exec("再次全量读取");

    new Timer() {
      @Override
      long mapper() {
        return addressRepository.getMyAddressByLikeName("北京").size();
      }
    }.exec("首次模糊查询");

    new Timer() {
      @Override
      long mapper() {
        return addressRepository.getMyAddressByLikeName("北京").size();
      }
    }.exec("再次模糊查询");

    new Timer() {
      @Override
      long mapper() {
        addressRepository.insertAddress(insertAddress());
        return 1;
      }
    }.exec("新增一条");

    new Timer() {
      @Override
      long mapper() {
        return addressRepository.getAllAddress().size();
      }
    }.exec("再次全量读取");

    new Timer() {
      @Override
      long mapper() {
        return addressRepository.getMyAddressByLikeName("北京").size();
      }
    }.exec("首次模糊查询");

    new Timer() {
      @Override
      long mapper() {
        return addressRepository.getMyAddressByLikeName("北京").size();
      }
    }.exec("再次模糊查询");
  }

  private AddressDO insertAddress() {
    Faker faker = new Faker(new Locale("zh-CN"));
    Address addr = faker.address();
    Name user = faker.name();
    AddressDO address = new AddressDO();
    address.setUuid(UUID.randomUUID().toString());
    address.setRemark("Faker");
    address.setName(addr.fullAddress());
    address.setLatitude(Double.valueOf(addr.latitude()));
    address.setLongitude(Double.valueOf(addr.longitude()));
    address.setPostcode(addr.zipCode());
    address.setCountry(addr.country());
    address.setCity(addr.city());
    return address;
  }

  class Timer {

    int count = 1;

    long mapper() {
      return 0;
    }

    void exec(String name) {
      List<Long> times = new ArrayList<>();
      long size = 0;
      for (int i = 0; i < count; i++) {
        long start = System.currentTimeMillis();
        size = mapper();
        long finish = System.currentTimeMillis();
        times.add(finish - start);
      }
      log.info("{} row={}, times={}", name, size, times.stream()
          .map(n -> n.toString())
          .collect(Collectors.joining(",")));
    }
  }

  @BeforeAll
  public static void setup() {
    mssql = new MySQLContainer<>(DockerImageName.parse("mysql:5.7"))
        .withUsername("test")
        .withPassword("test")
        .withDatabaseName("test");
    mssql.start();
  }

  @AfterAll
  public static void tearDown() {
    mssql.stop();
  }
}