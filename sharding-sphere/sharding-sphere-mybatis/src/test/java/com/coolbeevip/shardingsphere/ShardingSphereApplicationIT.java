package com.coolbeevip.shardingsphere;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.coolbeevip.shardingsphere.configuration.MybatisConfiguration;
import com.coolbeevip.shardingsphere.configuration.DataSourceConfiguration;
import com.coolbeevip.shardingsphere.mybatis.entities.CustomerDO;
import com.coolbeevip.shardingsphere.mybatis.entities.OrderDO;
import com.coolbeevip.shardingsphere.mybatis.repository.MybatisCustomerRepository;
import com.coolbeevip.shardingsphere.mybatis.repository.MybatisOrderRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
   * 测试写入订单数据异常
   */
  @Test
  public void insertOrderFailsTest() {
    CustomerDO customer = CustomerDO.builder()
        .id(UUID.randomUUID().toString())
        .firstName("Lei")
        .lastName("Zhang")
        .age(40)
        .createdAt(new Date())
        .lastUpdatedAt(new Date())
        .build();
    customerRepository.insert(customer);

    // total_price cannot be null exception
    Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
      OrderDO order = OrderDO.builder()
          .id(UUID.randomUUID().toString())
          .orderDesc("test")
          .customerId(customer.getId())
          .createdAt(new Date())
          .lastUpdatedAt(new Date())
          .build();
      orderRepository.insert(order);
    });
  }


  /**
   * 测试分库分表
   * t_orders 分库分表
   * t_customers 广播表，多个库中保持数据一致
   */
  @Test
  public void shardingDBAndTableTest() {
    List<CustomerDO> customerDOList = new ArrayList<>();

    for (int c = 0; c < 2; c++) {
      CustomerDO customer = CustomerDO.builder()
          .id(UUID.randomUUID().toString())
          .firstName("Lei " + c)
          .lastName("Zhang")
          .age(40 + c)
          .createdAt(new Date())
          .lastUpdatedAt(new Date())
          .build();
      assertThat(customerRepository.insert(customer), is(1));
      customerDOList.add(customer);

      for (int i = 0; i < 100; i++) {
        OrderDO order = OrderDO.builder()
            .id(UUID.randomUUID().toString())
            .orderDesc("test order")
            .customerId(customer.getId())
            .totalPrice(BigDecimal.valueOf(100 + i))
            .createdAt(new Date())
            .lastUpdatedAt(new Date())
            .build();
        assertThat(orderRepository.insert(order), is(1));
      }
    }

    List<OrderDO> orders = orderRepository.getAll();
    assertThat(orders.size(), is(200));

    customerDOList.stream().forEach(c -> {
      List<OrderDO> ordersOfCustomer = orderRepository
          .getOrdersOfCustomerOrderByTotalPrice(c.getId());
      ordersOfCustomer.stream().forEach(o -> {
        System.out.println(o.getTotalPrice());
      });

      assertThat(ordersOfCustomer.size(), is(100));
    });
  }
}

