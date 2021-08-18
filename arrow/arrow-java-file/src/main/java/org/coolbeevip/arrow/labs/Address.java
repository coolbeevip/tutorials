package org.coolbeevip.arrow.labs;

import java.lang.invoke.MethodHandles;
import org.coolbeevip.arrow.annotation.ArrowField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Address {

  @ArrowField(index = 0)
  private final String street;

  @ArrowField(index = 1)
  private final int streetNumber;

  @ArrowField(index = 2)
  private final String city;

  @ArrowField(index = 3)
  private final int postalCode;

  public Address(String street, int streetNumber, String city, int postalCode) {
    this.street = street;
    this.streetNumber = streetNumber;
    this.city = city;
    this.postalCode = postalCode;
  }

  public String getStreet() {
    return street;
  }

  public int getStreetNumber() {
    return streetNumber;
  }

  public String getCity() {
    return city;
  }

  public int getPostalCode() {
    return postalCode;
  }
}