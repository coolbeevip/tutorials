package org.coolbeevip.syslog;

import lombok.extern.slf4j.Slf4j;
import org.coolbeevip.syslog.tcp.TCPSyslogServerConfig;
import org.coolbeevip.syslog.udp.UDPSyslogServerConfig;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SyslogService {

  private final String host;
  private final int port;
  private final SyslogProtocol protocol;
  private static Map<String, ReceiveEventHandler> handlers = new ConcurrentHashMap<>();

  public static ReceiveEventHandler getReceiveEventHandler(String host, int port) {
    return handlers.get(getKey(host, port));
  }

  public SyslogService(String host, int port, SyslogProtocol protocol, ReceiveEventHandler handler) {
    SyslogServer.shutdown();
    this.host = host;
    this.port = port;
    this.protocol = protocol;

    log.info("Syslog Server protocol={} bind={} port={} handler={}", protocol.name(), host, port, handler.getClass().getSimpleName());
    SyslogServerConfigIF config = getSyslogConfig(protocol);
    config.setUseStructuredData(true);
    config.setHost(this.host);
    config.setPort(this.port);
    handlers.put(getKey(), handler);
    SyslogServer.createThreadedInstance(protocol.name().toLowerCase(), config);
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public SyslogProtocol getProtocol() {
    return protocol;
  }

  private String getKey() {
    return this.host + ":" + this.port;
  }

  private static String getKey(String host, int port) {
    return host + ":" + port;
  }

  private SyslogServerConfigIF getSyslogConfig(SyslogProtocol protocol) {
    SyslogServerConfigIF config = null;
    if (protocol.equals(SyslogProtocol.UDP)) {
      config = new UDPSyslogServerConfig();
    } else if (protocol.equals(SyslogProtocol.TCP)) {
      config = new TCPSyslogServerConfig();
    }
    return config;
  }

  public void shutdown() {
    SyslogServer.shutdown();
  }
}
