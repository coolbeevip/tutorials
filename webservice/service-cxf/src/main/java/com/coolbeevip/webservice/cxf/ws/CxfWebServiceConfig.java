package com.coolbeevip.webservice.cxf.ws;

import com.coolbeevip.webservice.cxf.ws.impl.HelloServiceImpl;
import com.coolbeevip.webservice.cxf.ws.interceptors.SoapRequestPreXmlInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.metrics.MetricsFeature;
import org.apache.cxf.metrics.MetricsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * @author zhanglei
 */
@Configuration
public class CxfWebServiceConfig {

  @Autowired
  private Bus bus;

  @Autowired
  private MetricsProvider metricsProvider;

  @Bean
  public Endpoint endpoint() {
    HelloService helloService = new HelloServiceImpl();
    EndpointImpl endpoint = new EndpointImpl(bus, helloService, null, null,
        new MetricsFeature[]{
            new MetricsFeature(metricsProvider)
        });
    endpoint.publish(HelloService.publishName);
    // TODO 此处可以增加服务端拦截器，修复请求信息（例如对方传入的 namespace）
    endpoint.getInInterceptors().add(new SoapRequestPreXmlInterceptor());
    return endpoint;
  }
}
