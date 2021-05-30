package org.coolbeevip.grpc.labs.demo;

public class OSInfo {

  private static String OS = System.getProperty("os.name").toLowerCase();

  public static boolean isLinux() {
    return OS.indexOf("linux") >= 0;
  }

  public static boolean isMacOS() {
    return OS.indexOf("mac") >= 0;
  }
}