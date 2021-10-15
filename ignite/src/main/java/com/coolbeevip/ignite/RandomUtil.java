package com.coolbeevip.ignite;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;

public class RandomUtil {

  static Faker faker = new Faker();

  public static Person randomPerson() {
    Address address = faker.address();
    return new Person(faker.idNumber().valid(), faker.name().fullName(),
        faker.number().numberBetween(1, 120),
        address.country(), address.cityName(), address.fullAddress(),
        faker.number().numberBetween(3000, 100000));
  }
}