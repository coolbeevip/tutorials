package com.coolbeevip.webservice.cxf.ws.impl;


import com.coolbeevip.webservice.cxf.ws.HelloService;
import lombok.extern.slf4j.Slf4j;

import javax.jws.WebService;

/**
 * @author zhanglei
 */
@Slf4j
@WebService(serviceName = HelloService.serviceName,
    portName = HelloService.serviceName,
    targetNamespace = HelloService.targetNamespace,
    endpointInterface = HelloService.endpointInterface)
public class HelloServiceImpl implements HelloService {

  @Override
  public String sayHello(String myname) {
    try {
      log.info("Hello " + myname);
      return "Hello " + myname;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
