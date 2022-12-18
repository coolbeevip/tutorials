package com.coolbeevip.design.patterns.structural.adapter;

public class Application {
  public static void main(String[] args) {
    Client client = new Adapter(new Service());
    System.out.println(client.getMessage());
  }
}
