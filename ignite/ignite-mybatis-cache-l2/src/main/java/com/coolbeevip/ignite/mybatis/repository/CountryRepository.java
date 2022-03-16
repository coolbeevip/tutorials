package com.coolbeevip.ignite.mybatis.repository;

import com.coolbeevip.ignite.mybatis.cache.IgniteCacheAdapter;
import com.coolbeevip.ignite.mybatis.entities.CountryDO;
import java.util.List;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Property;
import org.apache.ibatis.annotations.Select;

@Mapper
@CacheNamespace(implementation= IgniteCacheAdapter.class, properties = {
    @Property(name = "igniteConfigFile", value = "${ignite.config}")
})
public interface CountryRepository {

  @Select("select * from country")
  List<CountryDO> getAllCountry();

  @Select("select * from country where name like '%${name}%'")
  List<CountryDO> getCountryByLikeName(@Param("name") String name);

  @Insert("insert into country (uuid, name, capital, code) values "
      + "(#{uuid}, #{name}, #{capital}, #{code})")
  void insertCountry(CountryDO address);
}