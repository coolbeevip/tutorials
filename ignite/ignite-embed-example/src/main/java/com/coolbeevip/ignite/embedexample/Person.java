package com.coolbeevip.ignite.embedexample;

import lombok.ToString;

@ToString
public class Person {

  private final String id;
  private final int age;
  private final String name;
  private final String country;
  private final String city;
  private final String address;
  private final int salary;

  public Person(String id, String name, int age, String country,String city, String address, int salary) {
    this.id = id;
    this.age = age;
    this.name = name;
    this.country = country;
    this.city = city;
    this.address = address;
    this.salary = salary;
  }

  public String getId() {
    return id;
  }

  public int getAge() {
    return age;
  }

  public String getName() {
    return name;
  }

  public String getCountry() {
    return country;
  }

  public String getCity() {
    return city;
  }

  public String getAddress() {
    return address;
  }

  public int getSalary() {
    return salary;
  }
}