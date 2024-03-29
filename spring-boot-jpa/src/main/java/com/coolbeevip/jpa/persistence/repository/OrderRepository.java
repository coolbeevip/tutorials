package com.coolbeevip.jpa.persistence.repository;

import com.coolbeevip.jpa.persistence.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zhanglei
 */
public interface OrderRepository extends JpaRepository<Order, String> {

}
