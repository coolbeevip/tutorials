package com.coolbeevip.webservice.client.cxf;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;


public class CxfJaxWsDynamicClientTest {

  public static void main(String[] args) throws Exception {
    String name = "zhanglei";
    final Client client = getClient();
    Object[] res = new Object[0];
    try {
      res = client.invoke("sayHello", name);
      System.out.println(res[0].toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Client getClient() {
    JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
    String address = "http://localhost:6060/ws/HelloService?wsdl";
    return factory.createClient(address);
  }
}
