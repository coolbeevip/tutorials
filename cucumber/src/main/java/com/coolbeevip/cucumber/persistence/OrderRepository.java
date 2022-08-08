package com.coolbeevip.cucumber.persistence;


import com.coolbeevip.cucumber.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zhanglei
 */
public interface OrderRepository extends JpaRepository<Order, String> {

}
