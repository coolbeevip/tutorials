package com.coolbeevip.design.patterns.structural.bridge;

public class Application {
  public static void main(String[] args) {
    Remote remote1 = new Remote(new RadioDevice());
    System.out.println(remote1.volUp());
    System.out.println(remote1.volUp());
    System.out.println(remote1.volDown());


    Remote remote2 = new Remote(new TVDevice());
    System.out.println(remote2.volUp());
    System.out.println(remote2.volUp());
    System.out.println(remote2.volDown());

    AdvancedRemote remote3 = new AdvancedRemote(new TVDevice());
    System.out.println(remote3.setVolUp(100));
  }
}
