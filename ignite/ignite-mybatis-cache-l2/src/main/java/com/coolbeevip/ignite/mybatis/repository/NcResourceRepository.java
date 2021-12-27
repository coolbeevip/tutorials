package com.coolbeevip.ignite.mybatis.repository;

import com.coolbeevip.ignite.mybatis.cache.IgniteCacheAdapter;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Property;
import org.apache.ibatis.annotations.Select;

@Mapper
@CacheNamespace(implementation= IgniteCacheAdapter.class, properties = {
    @Property(name = "igniteConfigFile", value = "${ignite.config}")
})
public interface NcResourceRepository {

  @Select("select * from address")
  List<Map> getAllAddress();

  @Select("select * from address where name like '%美丽村%'")
  List<Map> getMyAddress();
}