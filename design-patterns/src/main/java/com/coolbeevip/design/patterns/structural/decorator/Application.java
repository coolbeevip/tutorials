package com.coolbeevip.design.patterns.structural.decorator;

import java.io.IOException;

public class Application {
  public static void main(String[] args) throws IOException {
    // 原始组件
    Component component = new ConcreteComponent();

    // 加密装饰器
    Component encryptionDecorator = new DecoratorEncryption(component);
    encryptionDecorator.writeData("hello".getBytes());
    System.out.println(new String(encryptionDecorator.readData()));


    // 压缩装饰器
    Component compressionDecorator = new DecoratorCompression(encryptionDecorator);
    compressionDecorator.writeData("hello".getBytes());
    System.out.println(new String(compressionDecorator.readData()));
  }
}
