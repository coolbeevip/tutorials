package com.coolbeevip.shardingsphere.jpa.persistence.repository;

import com.coolbeevip.shardingsphere.jpa.persistence.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zhanglei
 */
public interface OrderRepository extends JpaRepository<Order, String> {

}
