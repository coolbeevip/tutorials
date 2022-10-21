package com.coolbeevip.jpa;

import com.coolbeevip.jpa.persistence.audit.AuditEventType;
import com.coolbeevip.jpa.persistence.entities.Customer;
import com.coolbeevip.jpa.persistence.entities.Order;
import com.coolbeevip.jpa.persistence.repository.CustomerRepository;
import com.coolbeevip.jpa.persistence.repository.OrderRepository;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@Slf4j
@SpringBootTest(classes = {JpaApplication.class, JpaConfiguration.class})
public class JpaApplicationTestIT {

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  AuditCallbackQueueImpl auditEntityCallback;

  @Autowired
  HikariDataSource dataSource;

  @BeforeEach
  @SneakyThrows
  public void setup() {
    Customer zhanglei = Customer.builder().firstName("Lei").lastName("Zhang").build();
    customerRepository.save(zhanglei);
    for (int i = 0; i < 100; i++) {
      orderRepository.save(Order.builder().customer(zhanglei).totalPrice(BigDecimal.valueOf(i))
          .orderDesc("张磊的[" + i + "]订单").build());
    }

    Customer zhangboran = Customer.builder().firstName("Bo'Ran").lastName("Zhang").build();
    customerRepository.save(zhangboran);

    Customer kimran = Customer.builder().firstName("Ran").lastName("Kim").build();
    customerRepository.save(kimran);
    SECONDS.sleep(1);
  }

  @AfterEach
  @Transactional
  public void tearDown() {
    orderRepository.deleteAll();
    customerRepository.deleteAll();
  }

  @Test
  public void testInsertDefaultCurrentDate() {
    Customer insertCustomer = customerRepository
        .save(Customer.builder().firstName("You'Ran").lastName("Zhang").build());
    Assertions.assertNotNull(insertCustomer.getCreatedAt());
    Assertions.assertNotNull(insertCustomer.getLastUpdatedAt());
    Assertions.assertEquals(DateUtils.truncate(insertCustomer.getCreatedAt(), Calendar.SECOND),
        DateUtils.truncate(insertCustomer.getLastUpdatedAt(), Calendar.SECOND));
  }

  @Test
  @Transactional
  public void testAutoUpdateLastUpdatedDate() throws InterruptedException {
    Customer insertCustomer = customerRepository
        .save(Customer.builder().firstName("You'Ran").lastName("Zhang").build());
    SECONDS.sleep(1);
    // update
    insertCustomer.setAge(30);
    Customer updateCustomer = customerRepository.save(insertCustomer);
    Assertions.assertTrue(
        updateCustomer.getCreatedAt().getTime() == insertCustomer.getCreatedAt().getTime());
    Assertions.assertTrue(
        updateCustomer.getLastUpdatedAt().getTime() >= insertCustomer.getLastUpdatedAt().getTime());
  }

  @Test
  public void testDeleteById() {
    Optional<Customer> queryCustomer = customerRepository.findByFullName("Lei", "Zhang");
    customerRepository.deleteById(queryCustomer.get().getId());
    Assertions.assertFalse(customerRepository.findByFullName("Lei", "Zhang").isPresent());
  }

  @Test
  public void testDeleteByIdWithDerived() {
    int deleted = customerRepository.deleteByLastNameWithDerived("Kim");
    Assertions.assertEquals(deleted, 1);
    Assertions.assertEquals(customerRepository.findAll().size(), 2);
  }


  @Test
  @Transactional
  public void testDeleteByEntity() {
    orderRepository.deleteAll(); // TODO 不知为什么无法级联删除，所以暂时手动删除订单数据
    Optional<Customer> queryCustomer = customerRepository.findByFullName("Lei", "Zhang");
    customerRepository.delete(Customer.builder().id(queryCustomer.get().getId()).build());
    Assertions.assertFalse(customerRepository.findByFullName("Lei", "Zhang").isPresent());
  }

  @Test
  public void testUpdateAllEntityById() {
    Optional<Customer> queryCustomer = customerRepository.findByFullName("Lei", "Zhang");
    Customer oldCustomer = queryCustomer.get();
    oldCustomer.setAge(40);
    customerRepository.save(oldCustomer);
    Assertions.assertEquals(customerRepository.findByFullName("Lei", "Zhang").get().getAge(), 40);
  }

  @Test
  public void testModifyingQueries() {
    Optional<Customer> queryCustomer = customerRepository.findByFullName("Lei", "Zhang");
    int updated = customerRepository
        .updateFullNameById("Tom", "Zhang", queryCustomer.get().getId());
    Assertions.assertEquals(updated, 1);
    Optional<Customer> updatedCustomer = customerRepository.findById(queryCustomer.get().getId());
    Assertions.assertEquals(updatedCustomer.get().getFirstName(), "Tom");
    Assertions.assertEquals(updatedCustomer.get().getLastName(), "Zhang");
  }

  @Test
  public void testQueryAll() {

    List<Order> orders = orderRepository.findAll();
    for (Order order : orders) {
      log.info("order[{}], customerId={}", order.getId(), order.getCustomer().getId());
    }

    List<Customer> customers = customerRepository.findAll();
    Assertions.assertEquals(customers.size(), 3);
    for (Customer customer : customers) {
      log.info("customer[{}], orderSize={}", customer.getId(), customer.getOrders().size());
    }
  }

  @Test
  @SneakyThrows
  public void testDateBetween() {
    Date beginDate = new Date();
    Customer insertCustomer = customerRepository
        .save(Customer.builder().firstName("You'Ran").lastName("Zhang").build());
    Date endDate = new Date();
    List<Customer> queryCustomers = customerRepository.findByCreatedAtBetween(beginDate, endDate);
    Assertions.assertEquals(queryCustomers.get(0).getId(), insertCustomer.getId());
  }

  @Test
  public void testNamedParameters() {
    Optional<Customer> queryCustomer = customerRepository.findByFullName("Lei", "Zhang");
    Assertions.assertEquals(queryCustomer.get().getFirstName(), "Lei");
    Assertions.assertEquals(queryCustomer.get().getLastName(), "Zhang");
  }

  @Test
  public void testNamedQueries() {
    Optional<Customer> queryCustomer = customerRepository.findByFullName("Lei", "Zhang");
    Optional<Customer> customer = customerRepository.findById(queryCustomer.get().getId());
    Assertions.assertTrue(customer.isPresent());
    Assertions.assertEquals(customer.get().getFirstName(), "Lei");
    Assertions.assertEquals(customer.get().getLastName(), "Zhang");

    List<Customer> queryCustomers = customerRepository.findByLastName("Zhang");
    Assertions.assertEquals(queryCustomers.size(), 2);
    assertThat(
        queryCustomers.stream().map(c -> c.getFirstName()).collect(Collectors.<String>toList()),
        containsInAnyOrder("Lei", "Bo'Ran"));
  }

  @Test
  public void testSort() {
    List<Customer> customers = customerRepository.findAll(Sort.by("firstName"));
    Assertions.assertEquals(customers.get(0).getFirstName(), "Bo'Ran");
    Assertions.assertEquals(customers.get(1).getFirstName(), "Lei");
    Assertions.assertEquals(customers.get(2).getFirstName(), "Ran");
  }

  @Test
  public void testPageable() {
    Page<Customer> pageCustomers = customerRepository
        .findAll(PageRequest.of(0, 2, Sort.by("firstName")));
    Assertions.assertEquals(pageCustomers.getSize(), 2);
    Assertions.assertEquals(pageCustomers.getTotalElements(), 3l);
    Assertions.assertEquals(pageCustomers.getTotalPages(), 2);
    Assertions.assertEquals(pageCustomers.getContent().size(), 2);
    Assertions.assertEquals(pageCustomers.getContent().get(0).getFirstName(), "Bo'Ran");
    Assertions.assertEquals(pageCustomers.getContent().get(1).getFirstName(), "Lei");
  }

  @Test
  public void testPageableAndOrder() {
    Page<Customer> pageCustomers = customerRepository
        .findAll(PageRequest.of(0, 2, Sort.by("firstName").ascending().and(Sort.by("lastName"))));
    Assertions.assertEquals(pageCustomers.getSize(), 2);
    Assertions.assertEquals(pageCustomers.getTotalElements(), 3l);
    Assertions.assertEquals(pageCustomers.getTotalPages(), 2);
    Assertions.assertEquals(pageCustomers.getContent().size(), 2);
    Assertions.assertEquals(pageCustomers.getContent().get(0).getFirstName(), "Bo'Ran");
    Assertions.assertEquals(pageCustomers.getContent().get(1).getFirstName(), "Lei");
  }


  @Test
  public void testPageableAndWhereAndOrder() {
    cleanDB();

    List<String> firstNames = IntStream.range(0, 100).mapToObj(i -> "Lei" + i)
        .collect(Collectors.toList());
    AtomicInteger age = new AtomicInteger();
    firstNames.stream().forEach(firstName -> {
      customerRepository.save(Customer.builder().firstName(firstName).lastName("Zhang").age(
          age.getAndIncrement()).build());
    });

    Page<Customer> pageCustomers = customerRepository
        .findByLastName("Zhang", PageRequest.of(0, 50, Sort.by("age")));
    Assertions.assertEquals(pageCustomers.getSize(), 50);
    Assertions.assertEquals(pageCustomers.getTotalElements(), firstNames.size());
    Assertions.assertEquals(pageCustomers.getTotalPages(), firstNames.size() / 50);
    Assertions.assertEquals(pageCustomers.getContent().size(), 50);
    Assertions.assertEquals(
        pageCustomers.getContent().stream().map(c -> c.getFirstName()).collect(Collectors.toList()),
        firstNames.subList(0, 50));
  }

  @Test
  public void testAudit() {
    cleanDB();
    auditEntityCallback.getAuditRecords().clear();
    Customer customer = customerRepository
        .save(Customer.builder().firstName("Tom").lastName("Zhang").build());
    Awaitility.await().atMost(2, SECONDS).until(
        () -> !auditEntityCallback.getAuditRecords().isEmpty()
            && auditEntityCallback.getAuditRecords().getLast().getType() == AuditEventType.CREATED);

    customer = customerRepository.findById(customer.getId()).get();
    customer.setAge(50);
    customerRepository.save(customer);
    Awaitility.await().atMost(2, SECONDS).until(
        () -> !auditEntityCallback.getAuditRecords().isEmpty()
            && auditEntityCallback.getAuditRecords().getLast().getType() == AuditEventType.UPDATED);

    customer = customerRepository.findById(customer.getId()).get();
    customerRepository.delete(customer);
    Awaitility.await().atMost(2, SECONDS).until(
        () -> !auditEntityCallback.getAuditRecords().isEmpty()
            && auditEntityCallback.getAuditRecords().getLast().getType() == AuditEventType.DELETED);
  }

  @Test
  public void testOneToMany() {
    Customer insertCustomer = customerRepository
        .save(Customer.builder().firstName("OK").lastName("Zhang").build());
    orderRepository.save(
        Order.builder().customer(insertCustomer).totalPrice(BigDecimal.valueOf(1000))
            .orderDesc("测试订单").build());
    Optional<Customer> optionalCustomer = customerRepository.findById(insertCustomer.getId());
    Assertions.assertEquals(optionalCustomer.get().getOrders().size(), 1);
  }

  @Test
  public void testPool() {
    HikariConfigMXBean configMXBean = dataSource.getHikariConfigMXBean();
    HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
    log.info("config poolName {}", configMXBean.getPoolName());
    log.info("config maximumPoolSize {}", configMXBean.getMaximumPoolSize());
    log.info("config minimumIdle {}", configMXBean.getMinimumIdle());
    log.info("config connectionTimeout {}", configMXBean.getConnectionTimeout());
    log.info("config validationTimeout {}", configMXBean.getValidationTimeout());
    log.info("config maxLifetime {}", configMXBean.getMaxLifetime());
    log.info("config idleTimeout {}", configMXBean.getIdleTimeout());

    log.info("pool activeConnections {}", poolMXBean.getActiveConnections());
    log.info("pool totalConnections {}", poolMXBean.getTotalConnections());
    log.info("pool idleConnections {}", poolMXBean.getIdleConnections());
    log.info("pool threadsAwaitingConnection {}", poolMXBean.getThreadsAwaitingConnection());

  }

  private void cleanDB() {
    orderRepository.deleteAll();
    customerRepository.deleteAll();
  }
}
