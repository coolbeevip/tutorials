package com.coolbeevip.ignite.mybatis.repository;

import com.coolbeevip.ignite.mybatis.cache.IgniteCacheAdapter;
import com.coolbeevip.ignite.mybatis.entities.AddressDO;
import java.util.List;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Property;
import org.apache.ibatis.annotations.Select;

@Mapper
@CacheNamespace(implementation= IgniteCacheAdapter.class, properties = {
    @Property(name = "igniteConfigFile", value = "${ignite.config}")
})
public interface NcResourceRepository {

  @Select("select * from address")
  List<AddressDO> getAllAddress();

  @Select("select * from address where name like '%${name}%'")
  List<AddressDO> getMyAddressByLikeName(@Param("name") String name);


}