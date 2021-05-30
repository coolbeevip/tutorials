package org.coolbeevip.netty.labs.reactorext;


public class EchoServerMain {

  public static void main(String[] args) throws Exception {
    EchoServer echoServer = new EchoServer(8080);
    echoServer.start();
  }
}