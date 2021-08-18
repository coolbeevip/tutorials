package org.coolbeevip.arrow.labs;

import org.coolbeevip.arrow.annotation.ArrowField;

public class Person {

  @ArrowField(index = 0)
  private final String firstName;

  @ArrowField(index = 1)
  private final String lastName;

  @ArrowField(index = 2)
  private final int age;

  @ArrowField(index = 3)
  private final Address address;

  public Person(String firstName, String lastName, int age, Address address) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.age = age;
    this.address = address;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public int getAge() {
    return age;
  }

  public Address getAddress() {
    return address;
  }
}