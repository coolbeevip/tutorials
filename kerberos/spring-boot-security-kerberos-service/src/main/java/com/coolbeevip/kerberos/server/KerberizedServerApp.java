package com.coolbeevip.kerberos.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Paths;

@SpringBootApplication
public class KerberizedServerApp {
  static {
    System.setProperty("java.security.krb5.conf",
        Paths.get("/Users/zhanglei/Work/github/tutorials/kerberos/krb-workdir/krb5.conf")
            .normalize().toAbsolutePath().toString());
    System.setProperty("sun.security.krb5.debug", "true");
  }

  public static void main(String[] args) {

    SpringApplication.run(KerberizedServerApp.class, args);
  }
}
