package com.coolbeevip.ignite.mybatis;

import com.coolbeevip.ignite.mybatis.repository.NcResourceRepository;
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
    for (int i = 0; i < 10; i++) {
      long start = System.currentTimeMillis();
      long size = ncResourceRepository.getAllAddress().size();
      long finish = System.currentTimeMillis();
      log.info("row={}, use={}", size, finish - start);
    }
  }

  @Test
  public void test2() {
    for (int i = 0; i < 10; i++) {
      long start = System.currentTimeMillis();
      long size = ncResourceRepository.getMyAddress().size();
      long finish = System.currentTimeMillis();
      log.info("row={}, use={}", size, finish - start);
    }
  }
}