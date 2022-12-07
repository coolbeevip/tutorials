package com.coolbeevip.webservice.client.axis1;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.encoding.XMLType;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

public class Axis1ClientTest {

  public static void main(String[] args)
      throws ServiceException, MalformedURLException, RemoteException {
    call();
    callWithoutNamespace();
  }

  private static void callWithoutNamespace()
      throws ServiceException, MalformedURLException, RemoteException {
    String name = "zhanglei";
    Service service = new Service();
    Call call = (Call) service.createCall();
    call.setTargetEndpointAddress(new URL("http://localhost:6060/ws/HelloService"));
    call.setOperationName(new QName("sayHello"));
    call.addParameter(new QName("myname"), XMLType.XSD_STRING, ParameterMode.IN);
    call.setReturnType(XMLType.XSD_STRING);
    call.setUseSOAPAction(true);
    call.setOperationStyle(Style.RPC);
    call.setOperationUse(Use.LITERAL);
    String resultAsString = (String) call.invoke(new Object[]{name});
    System.out.println(resultAsString);
  }

  private static void call()
      throws ServiceException, MalformedURLException, RemoteException {
    String name = "zhanglei";
    Service service = new Service();
    Call call = (Call) service.createCall();
    call.setTargetEndpointAddress(new URL("http://localhost:6060/ws/HelloService"));
    call.setOperationName(new QName("http://api.cxf.webservice.coolbeevip.com/", "sayHello"));
    call.addParameter(new QName("myname"), XMLType.XSD_STRING, ParameterMode.IN);
    call.setReturnType(XMLType.XSD_STRING);
    call.setUseSOAPAction(true);
    call.setOperationStyle(Style.RPC);
    call.setOperationUse(Use.LITERAL);
    String resultAsString = (String) call.invoke(new Object[]{name});
    System.out.println(resultAsString);
  }
}
