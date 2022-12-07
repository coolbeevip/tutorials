package com.coolbeevip.webservice.client.wsdl2java;


import com.coolbeevip.webservice.cxf.api.Hello;
import com.coolbeevip.webservice.cxf.api.HelloService;

import java.net.MalformedURLException;
import java.net.URL;

public class WSDL2JavaClientTest {

  public static void main(String[] args) throws MalformedURLException {
    String name = "zhanglei";
    String address = "http://localhost:6060/ws/HelloService?wsdl";
    HelloService service = new HelloService(new URL(address));
    Hello client = service.getHelloService();
    String resultAsString = client.sayHello(name);
    System.out.println(resultAsString);
  }
}
