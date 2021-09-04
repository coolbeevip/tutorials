package com.coolbeevip.shardingsphere.mybatis.repository;

import com.coolbeevip.shardingsphere.mybatis.entities.OrderDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface MybatisOrderRepository {

  @Select("delete from t_orders")
  void deleteAll();

  @Insert("insert into t_orders(id,order_desc,customer_id,total_price,created_at,last_updated_at) values "
      + "(#{id},#{orderDesc},#{customerId},#{totalPrice},#{createdAt},#{lastUpdatedAt})")
  int insert(OrderDO order);
}