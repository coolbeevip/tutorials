package com.coolbeevip.ignite.mybatis;

import com.coolbeevip.ignite.mybatis.repository.NcResourceRepository;
import java.util.ArrayList;
import java.util.List;
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
  NcResourceRepository ncResourceRepository;

  @Test
  public void test() {
    new Temple() {
      @Override
      long mapper() {
        return ncResourceRepository.getAllAddress().size();
      }
    }.exec();

    new Temple() {
      @Override
      long mapper() {
        return ncResourceRepository.getMyAddressByLikeName("西藏").size();
      }
    }.exec();

    new Temple() {
      @Override
      long mapper() {
        return ncResourceRepository.getMyAddressByLikeName("湖北").size();
      }
    }.exec();

    new Temple() {
      @Override
      long mapper() {
        return ncResourceRepository.getMyAddressByLikeName("拉萨").size();
      }
    }.exec();
  }

  class Temple {

    int count = 10;

    long mapper() {
      return 0;
    }

    void exec() {
      List<Long> times = new ArrayList<>();
      long size = 0;
      for (int i = 0; i < count; i++) {
        long start = System.currentTimeMillis();
        size = mapper();
        long finish = System.currentTimeMillis();
        times.add(finish - start);
      }
      log.info("row={}, times={}", size, times.stream()
          .map(n -> n.toString())
          .collect(Collectors.joining(",")));
    }
  }
}