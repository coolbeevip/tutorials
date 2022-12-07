package com.coolbeevip.webservice.cxf.ws.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

import java.io.InputStream;

/**
 * 请求拦截器，可以用于实现拦截请求并修改请求的 Soap XML
 *
 * @author zhanglei
 */
@Slf4j
public class SoapRequestPreXmlInterceptor extends AbstractSoapInterceptor {

  public SoapRequestPreXmlInterceptor() {
    super(Phase.PRE_STREAM);
    addBefore(SoapPreProtocolOutInterceptor.class.getName());
  }

  @Override
  public void handleMessage(SoapMessage message) throws Fault {
    boolean isInbound = false;
    isInbound = message == message.getExchange().getInMessage();
    if (isInbound) {
      try {
        InputStream is = message.getContent(InputStream.class);
        String currentEnvelopeMessage = IOUtils.toString(is, "UTF-8");
        IOUtils.closeQuietly(is);
        String res = fixedOpNameTargetNamespace(currentEnvelopeMessage);
        is = IOUtils.toInputStream(res, "UTF-8");
        message.setContent(InputStream.class, is);
        IOUtils.closeQuietly(is);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  /**
   * 用于替换入栈请求的 XML
   * 例如：找到 <sayHello> 替换为 <sayHello xmlns="http://api.cxf.examples.ai.com/">
   */
  private String fixedOpNameTargetNamespace(String currentEnvelopeMessage) {
    String fixedInboundMessage;
    if (currentEnvelopeMessage.contains("<sayHello xmlns=\"\">")) {
      log.info("Origin Inbound message {}", currentEnvelopeMessage);
      fixedInboundMessage = currentEnvelopeMessage
          .replace("<sayHello xmlns=\"\">", "<sayHello xmlns=\"http://api.cxf.webservice.coolbeevip.com/\">");
      fixedInboundMessage = fixedInboundMessage
          .replace("<myname xsi:type=\"xsd:string\">", "<myname xsi:type=\"xsd:string\" xmlns=\"\">");
      log.info("Change Inbound message {}", fixedInboundMessage);
    } else {
      fixedInboundMessage = currentEnvelopeMessage;
      log.info("Origin Inbound message {}", fixedInboundMessage);
    }
    return fixedInboundMessage;
  }
}
