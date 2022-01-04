package com.coolbeevip.ignite.mybatis;

import com.coolbeevip.ignite.mybatis.entities.AddressDO;
import com.coolbeevip.ignite.mybatis.repository.AddressRepository;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = {MybatisCacheIgniteApplication.class,
    MybatisCacheIgniteConfiguration.class})
public class MybatisCacheIgniteApplicationTest {

  @Autowired
  AddressRepository ncResourceRepository;

  @Test
  public void test() {
    new Timer() {
      @Override
      long mapper() {
        return ncResourceRepository.getAllAddress().size();
      }
    }.exec("首次全量读取");

    new Timer() {
      @Override
      long mapper() {
        return ncResourceRepository.getAllAddress().size();
      }
    }.exec("再次全量读取");

    new Timer() {
      @Override
      long mapper() {
        return ncResourceRepository.getMyAddressByLikeName("北京").size();
      }
    }.exec("首次模糊查询");

    new Timer() {
      @Override
      long mapper() {
        return ncResourceRepository.getMyAddressByLikeName("北京").size();
      }
    }.exec("再次模糊查询");

    new Timer() {
      @Override
      long mapper() {
        ncResourceRepository.insertAddress(insertAddress());
        return 1;
      }
    }.exec("新增一条");


    new Timer() {
      @Override
      long mapper() {
        return ncResourceRepository.getAllAddress().size();
      }
    }.exec("再次全量读取");

    new Timer() {
      @Override
      long mapper() {
        return ncResourceRepository.getMyAddressByLikeName("北京").size();
      }
    }.exec("首次模糊查询");

    new Timer() {
      @Override
      long mapper() {
        return ncResourceRepository.getMyAddressByLikeName("北京").size();
      }
    }.exec("再次模糊查询");
  }

  private AddressDO insertAddress(){
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
}