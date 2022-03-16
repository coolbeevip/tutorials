package com.coolbeevip.shardingsphere.mybatis.repository;

import com.coolbeevip.shardingsphere.mybatis.entities.OrderDO;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface MybatisOrderRepository {

  @Select("delete from t_orders")
  void deleteAll();

  @Insert("insert into t_orders(id,order_desc,customer_id,total_price,created_at,last_updated_at) values "
      + "(#{id},#{orderDesc},#{customerId},#{totalPrice},#{createdAt},#{lastUpdatedAt})")
  int insert(OrderDO order);

  @Select("select * from t_orders")
  List<OrderDO> getAll();

  @Select("select * from t_orders where customer_id=#{customerId} order by total_price")
  List<OrderDO> getOrdersOfCustomerOrderByTotalPrice(@Param("customerId") String customerId);
}