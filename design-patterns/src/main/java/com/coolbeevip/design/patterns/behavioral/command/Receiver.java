package com.coolbeevip.design.patterns.behavioral.command;

import java.util.ArrayList;
import java.util.List;

/**
 * 接收者
 */
public class Receiver {
  private List<String> values = new ArrayList<>();

  public void receiver(String value) {
    values.add(value);
    System.out.println("收到: " + value);
  }
}
