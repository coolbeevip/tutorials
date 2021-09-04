package com.coolbeevip.shardingsphere;

import com.coolbeevip.shardingsphere.configuration.MybatisConfiguration;
import com.coolbeevip.shardingsphere.configuration.DataSourceConfiguration;
import com.coolbeevip.shardingsphere.mybatis.entities.CustomerDO;
import com.coolbeevip.shardingsphere.mybatis.entities.OrderDO;
import com.coolbeevip.shardingsphere.mybatis.repository.MybatisCustomerRepository;
import com.coolbeevip.shardingsphere.mybatis.repository.MybatisOrderRepository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest(classes = {ShardingSphereApplication.class,
    DataSourceConfiguration.class,
    MybatisConfiguration.class})
public class ShardingSphereApplicationIT {

  @Autowired
  MybatisCustomerRepository customerRepository;

  @Autowired
  MybatisOrderRepository orderRepository;

  @BeforeEach
  @Transactional
  public void setup() {
    orderRepository.deleteAll();
    customerRepository.deleteAll();
  }


  /**
   * 测试分库分表
   * t_orders 分库分表
   * t_customers 广播表，多个库中保持数据一致
   */
  @Test
  public void insertCustomerTest() {
    for (int c = 0; c < 2; c++) {
      CustomerDO customer = CustomerDO.builder()
          .id(UUID.randomUUID().toString())
          .firstName("Lei")
          .lastName("Zhang")
          .age(40)
          .createdAt(new Date())
          .lastUpdatedAt(new Date())
          .build();
      int num = customerRepository.insert(customer);
      log.info("insert {} row", num);

      for (int i = 0; i < 10; i++) {
        OrderDO order = OrderDO.builder()
            .id(UUID.randomUUID().toString())
            .orderDesc("test order")
            .customerId(customer.getId())
            .totalPrice(BigDecimal.valueOf(100))
            .createdAt(new Date())
            .lastUpdatedAt(new Date())
            .build();
        num = orderRepository.insert(order);
        log.info("insert {} row", num);
      }
    }
  }
}

