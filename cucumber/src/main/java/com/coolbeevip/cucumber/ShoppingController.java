package com.coolbeevip.cucumber;

import com.coolbeevip.cucumber.entities.Customer;
import com.coolbeevip.cucumber.entities.Order;
import com.coolbeevip.cucumber.persistence.CustomerRepository;
import com.coolbeevip.cucumber.persistence.OrderRepository;
import com.coolbeevip.cucumber.view.OrderView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collection;

@RestController
public class ShoppingController {

  final CustomerRepository customerRepository;

  final OrderRepository orderRepository;

  public ShoppingController(CustomerRepository customerRepository, OrderRepository orderRepository) {
    this.customerRepository = customerRepository;
    this.orderRepository = orderRepository;
  }

  @PostMapping
  public Order addOrder(@RequestBody OrderView orderView) {
    Order order = Order.builder()
        .customer(this.getCustomerById(orderView.getCustomerId()))
        .price(orderView.getPrice())
        .itemName(orderView.getItemName())
        .build();
    return this.orderRepository.save(order);
  }

  @PostMapping
  public Customer addCustomer(String name, BigDecimal amount) {
    Customer customer = Customer.builder()
        .name(name)
        .amount(amount)
        .build();
    return this.customerRepository.save(customer);
  }

  @GetMapping
  public Customer getCustomerById(String id) {
    return this.customerRepository.getById(id);
  }

  @GetMapping
  public Collection<Order> getOrdersByCustomer(String id) {
    Customer customer = this.customerRepository.getById(id);
    return customer.getOrders();
  }
}
