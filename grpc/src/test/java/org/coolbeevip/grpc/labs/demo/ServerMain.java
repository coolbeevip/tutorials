package org.coolbeevip.grpc.labs.demo;

public class ServerMain {
  public static void main(String[] args) throws Exception {
    ExchangeServer server = new ExchangeServer(8081);
    server.start();
  }
}