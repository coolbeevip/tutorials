package com.coolbeevip.ignite.mybatis;

import com.coolbeevip.ignite.mybatis.entities.AddressDO;
import com.coolbeevip.ignite.mybatis.entities.CountryDO;
import com.coolbeevip.ignite.mybatis.repository.AddressRepository;
import com.coolbeevip.ignite.mybatis.repository.CountryRepository;
import com.github.javafaker.Address;
import com.github.javafaker.Country;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = {MybatisCacheIgniteApplication.class,
    MybatisCacheIgniteConfiguration.class})
public class DataTest {

  @Autowired
  AddressRepository addressRepository;

  @Autowired
  CountryRepository countryRepository;

  Faker faker = new Faker(new Locale("zh-CN"));

  @Test
  public void initCountryTest() {
    for(int i=0;i<50;i++){
      Country country = faker.country();
      CountryDO c = new CountryDO();
      c.setUuid(UUID.randomUUID().toString());
      c.setName(country.name());
      countryRepository.insertCountry(c);
    }
  }

  @Test
  public void initAddressTest() {
    int total = 100000;
    int batchSize = 5000;
    List<AddressDO> addresses = new ArrayList<>();
    while (total>0) {
      if (addresses.size() < batchSize) {
        Address addr = faker.address();
        AddressDO address = new AddressDO();
        address.setUuid(UUID.randomUUID().toString());
        address.setRemark("Faker");
        address.setName(addr.fullAddress());
        address.setLatitude(Double.valueOf(addr.latitude()));
        address.setLongitude(Double.valueOf(addr.longitude()));
        address.setPostcode(addr.zipCode());
        address.setCountry(addr.country());
        address.setCity(addr.city());
        addresses.add(address);
        total--;
      } else {
        long begin = System.currentTimeMillis();
        addressRepository.insertAddresses(addresses);
        addresses.clear();
        long end = System.currentTimeMillis();
        log.info("batch save {}ms",end-begin);
      }
    }
    if(addresses.size()>0){
      addressRepository.insertAddresses(addresses);
    }
  }

}