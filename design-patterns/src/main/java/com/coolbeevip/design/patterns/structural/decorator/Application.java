package com.coolbeevip.design.patterns.structural.decorator;

import java.io.IOException;

public class Application {
  public static void main(String[] args) throws IOException {
    Component component = new ConcreteComponent();
    Component encryptionDecorator = new DecoratorEncryption(component);
    encryptionDecorator.writeData("hello".getBytes());
    System.out.println(new String(encryptionDecorator.readData()));


    Component compressionDecorator = new DecoratorCompression(encryptionDecorator);
    compressionDecorator.writeData("hello".getBytes());
    System.out.println(new String(compressionDecorator.readData()));
  }
}
