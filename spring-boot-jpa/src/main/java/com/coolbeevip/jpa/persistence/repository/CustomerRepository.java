package com.coolbeevip.jpa.persistence.repository;

import com.coolbeevip.jpa.persistence.model.Customer;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 * @author zhanglei
 */
public interface CustomerRepository extends CrudRepository<Customer, Long> {

  List<Customer> findByLastName(String lastName);

  Customer findById(long id);
}
