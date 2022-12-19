package com.coolbeevip.design.patterns.structural.proxy;

import java.io.IOException;

public class Application {
  public static void main(String[] args) throws IOException {
    Video video = new VideoService();
    System.out.println(video.downloadVideo("1"));

    Video proxy = new VideoProxy();
    System.out.println(proxy.downloadVideo("1"));
    System.out.println(proxy.downloadVideo("1"));
  }

}
