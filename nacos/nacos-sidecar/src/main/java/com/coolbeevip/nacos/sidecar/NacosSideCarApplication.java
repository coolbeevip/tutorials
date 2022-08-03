package com.coolbeevip.nacos.sidecar;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NacosSideCarApplication {
  private static final Logger log = LoggerFactory.getLogger(NacosSideCarApplication.class);
  private static final ReentrantLock LOCK = new ReentrantLock();
  private static final Condition STOP = LOCK.newCondition();

  public static void main(String[] args) throws NacosException, InterruptedException {

    String SERVER_ADDR = System.getProperty("SERVER_ADDR");//"127.0.0.1:8848";
    String NAMESPACE = System.getProperty("NAMESPACE");//"public";
    String GROUP_NAME = System.getProperty("GROUP_NAME");//"testService";
    String SERVICE_NAME = System.getProperty("SERVICE_NAME");//"testService";
    String SERVICE_IP = System.getProperty("SERVICE_IP");//"10.19.88.60";
    int SERVICE_PORT = Integer.parseInt(System.getProperty("SERVICE_PORT"));//8080;
    String USERNAME = System.getProperty("USERNAME");
    String PASSWORD = System.getProperty("PASSWORD");

    Properties properties = new Properties();
    properties.put(PropertyKeyConst.SERVER_ADDR, SERVER_ADDR);
    properties.put(PropertyKeyConst.NAMESPACE, NAMESPACE);

    if(USERNAME!=null && PASSWORD!=null){
      properties.put(PropertyKeyConst.USERNAME, USERNAME);
      properties.put(PropertyKeyConst.PASSWORD, PASSWORD);
    }

    NamingService naming = NacosFactory.createNamingService(properties);
    naming.registerInstance(SERVICE_NAME, GROUP_NAME, SERVICE_IP, SERVICE_PORT);
    log.info("NACOS 2 SideCar register NAMESPACE={} NAMESPACE={} GROUP_NAME={} SERVICE_NAME={} SERVICE_IP={} SERVICE_PORT={} Succeed!", SERVER_ADDR, NAMESPACE, GROUP_NAME, SERVICE_NAME, SERVICE_IP, SERVICE_PORT);
    synchronized (naming) {
      naming.wait();
    }
  }

}
