package com.coolbeevip.maven;

import java.security.CodeSource;

public class Main {
  public static void main(String[] args) {

    CodeSource codeSource = new org.joda.time.DateTime().getClass().getProtectionDomain().getCodeSource();
    System.out.println("unshaded = " + codeSource);

    codeSource = new my.elasticsearch.joda.time.DateTime().getClass().getProtectionDomain().getCodeSource();
    System.out.println("shaded = " + codeSource);
  }
}
