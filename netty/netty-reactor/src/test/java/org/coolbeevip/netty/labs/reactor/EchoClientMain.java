package org.coolbeevip.netty.labs.reactor;


public class EchoClientMain {

  public static void main(String[] args) throws Exception {
    EchoClient echoClient = new EchoClient("0.0.0.0", 8080);
    echoClient.start();
  }
}