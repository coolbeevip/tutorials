package com.coolbeevip.ignite.mybatis.repository;

import com.coolbeevip.ignite.mybatis.cache.IgniteCacheAdapter;
import com.coolbeevip.ignite.mybatis.entities.AddressDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Property;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@CacheNamespace(implementation = IgniteCacheAdapter.class, properties = {
    @Property(name = "igniteConfigFile", value = "${ignite.config}")
})
public interface AddressRepository {

  @Select("select * from address")
  List<AddressDO> getAllAddress();

  @Select("select * from address where name like '%${name}%'")
  List<AddressDO> getMyAddressByLikeName(@Param("name") String name);

  @Insert("insert into address (uuid, name, postcode, remark, latitude, longitude, country, city) values "
      + "(#{uuid}, #{name}, #{postcode}, #{remark}, #{latitude}, #{longitude}, #{country}, #{city})")
  void insertAddress(AddressDO address);

  @Insert("<script>"
      + "insert into address (uuid, name, postcode, remark, latitude, longitude, country, city) values "
      + "<foreach collection =\"addresses\" item=\"addr\" separator =\",\">"
      + "(#{addr.uuid}, #{addr.name}, #{addr.postcode}, #{addr.remark}, #{addr.latitude}, #{addr.longitude}, #{addr.country}, #{addr.city})"
      + "</foreach>"
      + "</script>")
  void insertAddresses(@Param("addresses") List<AddressDO> addresses);
}