package com.coolbeevip.webservice.cxf.ws;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * @author zhanglei
 */
@WebService(targetNamespace = HelloService.targetNamespace, name = "Hello")
public interface HelloService {

  String endpointInterface = "com.coolbeevip.webservice.cxf.ws.HelloService";
  String targetNamespace = "http://api.cxf.webservice.coolbeevip.com/";
  String serviceName = "HelloService";
  String publishName = "/HelloService";

  @WebResult(name = "result") // 此处不要设置 targetNamespace，否则生成的 wsdl sayHelloResponse 对象将被加上 form="qualified"
  @RequestWrapper(localName = "sayHello", targetNamespace = HelloService.targetNamespace)
  @ResponseWrapper(targetNamespace = HelloService.targetNamespace)
  String sayHello(@WebParam(name = "myname") String myname);

}
