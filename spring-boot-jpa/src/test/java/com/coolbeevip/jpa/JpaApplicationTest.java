package com.coolbeevip.jpa;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.coolbeevip.jpa.persistence.audit.AuditEntityCallback;
import com.coolbeevip.jpa.persistence.audit.AuditEventType;
import com.coolbeevip.jpa.persistence.model.Customer;
import com.coolbeevip.jpa.persistence.repository.CustomerRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Slf4j
@SpringBootTest(classes = {JpaApplication.class, JpaConfiguration.class})
public class JpaApplicationTest {

  @Autowired
  CustomerRepository repository;

  @Autowired
  AuditCallbackQueueImpl auditEntityCallback;

  @BeforeEach
  public void setup() {
    repository.save(Customer.builder().firstName("Lei").lastName("Zhang").build());
    repository.save(Customer.builder().firstName("Bo'Ran").lastName("Zhang").build());
    repository.save(Customer.builder().firstName("Ran").lastName("Kim").build());
  }

  @AfterEach
  public void tearDown() {
    repository.deleteAll();
  }

  @Test
  public void testInsertDefaultCurrentDate() {
    Customer insertCustomer = repository
      .save(Customer.builder().firstName("You'Ran").lastName("Zhang").build());
    Assertions.assertNotNull(insertCustomer.getCreatedAt());
    Assertions.assertNotNull(insertCustomer.getLastTouchAt());
    Assertions.assertEquals(DateUtils.truncate(insertCustomer.getCreatedAt(), Calendar.SECOND),
      DateUtils.truncate(insertCustomer.getLastTouchAt(), Calendar.SECOND));
  }

  @Test
  public void testAutoUpdateLastTouchDate() throws InterruptedException {
    Customer insertCustomer = repository
      .save(Customer.builder().firstName("You'Ran").lastName("Zhang").build());
    TimeUnit.SECONDS.sleep(1);
    // update
    insertCustomer.setAge(30);
    Customer updateCustomer = repository.save(insertCustomer);
    Assertions.assertTrue(
      updateCustomer.getCreatedAt().getTime() == insertCustomer.getCreatedAt().getTime());
    Assertions.assertTrue(
      updateCustomer.getLastTouchAt().getTime() > insertCustomer.getLastTouchAt().getTime());
  }

  @Test
  public void testDeleteById() {
    Optional<Customer> queryCustomer = repository.findByFullName("Lei", "Zhang");
    repository.deleteById(queryCustomer.get().getId());
    Assertions.assertFalse(repository.findByFullName("Lei", "Zhang").isPresent());
  }

  @Test
  public void testDeleteByIdWithDerived() {
    int deleted = repository.deleteByLastNameWithDerived("Zhang");
    Assertions.assertEquals(deleted, 2);
    Assertions.assertEquals(repository.findAll().size(), 1);
  }


  @Test
  public void testDeleteByEntity() {
    Optional<Customer> queryCustomer = repository.findByFullName("Lei", "Zhang");
    repository.delete(Customer.builder().id(queryCustomer.get().getId()).build());
    Assertions.assertFalse(repository.findByFullName("Lei", "Zhang").isPresent());
  }

  @Test
  public void testUpdateAllEntityById() {
    Optional<Customer> queryCustomer = repository.findByFullName("Lei", "Zhang");
    Customer oldCustomer = queryCustomer.get();
    oldCustomer.setAge(40);
    repository.save(oldCustomer);
    Assertions.assertEquals(repository.findByFullName("Lei", "Zhang").get().getAge(), 40);
  }

  @Test
  public void testModifyingQueries() {
    Optional<Customer> queryCustomer = repository.findByFullName("Lei", "Zhang");
    int updated = repository.updateFullNameById("Tom", "Zhang", queryCustomer.get().getId());
    Assertions.assertEquals(updated, 1);
    Optional<Customer> updatedCustomer = repository.findById(queryCustomer.get().getId());
    Assertions.assertEquals(updatedCustomer.get().getFirstName(), "Tom");
    Assertions.assertEquals(updatedCustomer.get().getLastName(), "Zhang");
  }

  @Test
  public void testQueryAll() {
    List<Customer> customers = repository.findAll();
    Assertions.assertEquals(customers.size(), 3);
    for (Customer customer : customers) {
      log.info(customer.toString());
    }
  }

  @Test
  public void testDateBetween() {
    Date beginDate = new Date();
    Customer insertCustomer = repository
      .save(Customer.builder().firstName("You'Ran").lastName("Zhang").build());
    Date endDate = new Date();
    List<Customer> queryCustomers = repository.findByCreatedAtBetween(beginDate, endDate);
    Assertions.assertEquals(queryCustomers.get(0).getId(), insertCustomer.getId());
  }

  @Test
  public void testNamedParameters() {
    Optional<Customer> queryCustomer = repository.findByFullName("Lei", "Zhang");
    Assertions.assertEquals(queryCustomer.get().getFirstName(), "Lei");
    Assertions.assertEquals(queryCustomer.get().getLastName(), "Zhang");
  }

  @Test
  public void testNamedQueries() {
    Optional<Customer> queryCustomer = repository.findByFullName("Lei", "Zhang");
    Optional<Customer> customer = repository.findById(queryCustomer.get().getId());
    Assertions.assertTrue(customer.isPresent());
    Assertions.assertEquals(customer.get().getFirstName(), "Lei");
    Assertions.assertEquals(customer.get().getLastName(), "Zhang");

    List<Customer> queryCustomers = repository.findByLastName("Zhang");
    Assertions.assertEquals(queryCustomers.size(), 2);
    assertThat(
      queryCustomers.stream().map(c -> c.getFirstName()).collect(Collectors.<String>toList()),
      containsInAnyOrder("Lei", "Bo'Ran"));
  }

  @Test
  public void testOrder() {
    List<Customer> customers = repository.findAll(Sort.by("firstName"));
    Assertions.assertEquals(customers.get(0).getFirstName(), "Bo'Ran");
    Assertions.assertEquals(customers.get(1).getFirstName(), "Lei");
    Assertions.assertEquals(customers.get(2).getFirstName(), "Ran");
  }

  @Test
  public void testPageable() {
    Page<Customer> pageCustomers = repository.findAll(PageRequest.of(0, 2));
    Assertions.assertEquals(pageCustomers.getSize(), 2);
    Assertions.assertEquals(pageCustomers.getTotalElements(), 3l);
    Assertions.assertEquals(pageCustomers.getTotalPages(), 2);
    Assertions.assertEquals(pageCustomers.getContent().size(), 2);
    Assertions.assertEquals(pageCustomers.getContent().get(0).getFirstName(), "Lei");
    Assertions.assertEquals(pageCustomers.getContent().get(1).getFirstName(), "Bo'Ran");
  }

  @Test
  public void testPageableAndOrder() {
    Page<Customer> pageCustomers = repository
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
    repository.deleteAll();

    List<String> firstNames = IntStream.range(0, 100).mapToObj(i -> "Lei" + i)
      .collect(Collectors.toList());
    firstNames.stream().forEach(firstName -> {
      repository.save(Customer.builder().firstName(firstName).lastName("Zhang").build());
    });

    Page<Customer> pageCustomers = repository.findByLastName("Zhang", PageRequest.of(0, 50));
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
    repository.deleteAll();
    auditEntityCallback.getAuditRecords().clear();
    Customer customer = repository
      .save(Customer.builder().firstName("Tom").lastName("Zhang").build());
    Assertions.assertEquals(auditEntityCallback.getAuditRecords().getLast().getType(), AuditEventType.CREATED);

    customer.setAge(50);
    repository.save(customer);
    Assertions.assertEquals(auditEntityCallback.getAuditRecords().getLast().getType(), AuditEventType.UPDATED);

    repository.delete(customer);
    Assertions.assertEquals(auditEntityCallback.getAuditRecords().getLast().getType(), AuditEventType.DELETED);
  }
}
