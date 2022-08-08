package com.coolbeevip.cucumber.persistence;


import com.coolbeevip.cucumber.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author zhanglei
 */
public interface CustomerRepository extends JpaRepository<Customer, String> {

  List<Customer> findByName(String lastName);

}
