package com.coolbeevip.webservice.client.axis2;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import javax.xml.namespace.QName;

public class Axis2ClientTest {

  public static void main(String[] args) {
    try {
      OMFactory fac = OMAbstractFactory.getOMFactory();
      OMNamespace ns = fac.createOMNamespace("http://api.cxf.webservice.coolbeevip.com/", "ns1");
      OMElement operation = fac.createOMElement("sayHello", ns);
      OMElement value = fac.createOMElement(new QName("myname"));
      value.setText("zhanglei");
      operation.addChild(value);
      Options options = new Options();
      ServiceClient client = new ServiceClient();
      options.setTo(new EndpointReference("http://localhost:6060/ws/HelloService"));
      client.setOptions(options);
      OMElement result = client.sendReceive(operation);
      System.out.println(result.toString());
    } catch (AxisFault e) {
      e.printStackTrace();
    }

  }
}
