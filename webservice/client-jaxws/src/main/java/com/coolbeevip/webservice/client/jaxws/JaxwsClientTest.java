package com.coolbeevip.webservice.client.jaxws;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class JaxwsClientTest {

  public static void main(String[] args) throws MalformedURLException, SOAPException {
    String name = "zhanglei";
    String address = "http://localhost:6060/ws/HelloService";
    Service service = Service.create(new URL(address + "?wsdl"), new QName("http://api.cxf.webservice.coolbeevip.com/", "HelloService"));
    Dispatch<SOAPMessage> disp = service
        .createDispatch(new QName("http://api.cxf.webservice.coolbeevip.com/", "HelloService"),
            SOAPMessage.class, Mode.MESSAGE);


    SOAPMessage requestSOAPMessage = requestMessage(name);
    System.out.println(getRawXml(requestSOAPMessage));
    SOAPMessage responseSOAPMessage = disp.invoke(requestSOAPMessage);

    System.out.println(getRawXml(responseSOAPMessage));
    System.out.println(responseSOAPMessage.getSOAPBody().getFirstChild().getTextContent());
  }

  private static SOAPMessage requestMessage(String name) throws SOAPException {
    MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
    SOAPMessage request = mf.createMessage();
    SOAPPart part = request.getSOAPPart();
    SOAPEnvelope env = part.getEnvelope();
    SOAPBody body = env.getBody();
    SOAPElement operation = body.addChildElement("sayHello", "ns1", "http://api.cxf.webservice.coolbeevip.com/");
    SOAPElement value = operation.addChildElement("myname");
    value.addTextNode(name);
    request.saveChanges();
    return request;
  }

  private static String getRawXml(SOAPMessage soapMessage) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      soapMessage.writeTo(out);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new String(out.toByteArray());
  }
}
