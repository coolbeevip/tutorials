package com.coolbeevip.shardingsphere.mybatis.repository;

import com.coolbeevip.shardingsphere.mybatis.entities.CustomerDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface MybatisCustomerRepository {

  @Select("delete from t_customers")
  void deleteAll();

  @Insert("insert into t_customers(id,first_name,last_name,age,created_at,last_updated_at) values "
      + "(#{id},#{firstName},#{lastName},#{age},#{createdAt},#{lastUpdatedAt})")
  int insert(CustomerDO customer);
}