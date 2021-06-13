package com.coolbeevip.test;

import java.util.LinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SocketUtils;

/**
 * @author zhanglei
 */
@Slf4j
public class TutorialsTestSuite {

  static TutorialsTestSuite INSTANCE;

  int cacheSize = 50;
  LinkedList<Integer> unusedPorts = new LinkedList();

  public TutorialsTestSuite() {
    if (unusedPorts.isEmpty()) {
      unusedPorts.addAll(SocketUtils.findAvailableTcpPorts(cacheSize, 40000, 65535));
      log.info("生成测试用随机端口 {} 个", unusedPorts.size());
      unusedPorts.forEach(p -> log.info("unuserd port: {}", p));
    }
  }

  public synchronized static TutorialsTestSuite getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new TutorialsTestSuite();
    }
    return INSTANCE;
  }

  public synchronized int findAvailableTcpPort() {
    int port = unusedPorts.pop();
    log.info("获取随机端口 {}, 剩余可用 {}", port, unusedPorts.size());
    return port;
  }
}
