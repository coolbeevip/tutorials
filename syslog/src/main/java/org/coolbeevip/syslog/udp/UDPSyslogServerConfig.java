package org.coolbeevip.syslog.udp;

import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.productivity.java.syslog4j.server.impl.net.udp.UDPNetSyslogServerConfig;


public class UDPSyslogServerConfig extends UDPNetSyslogServerConfig {

  private static final long serialVersionUID = 1L;

  @Override
  public Class<? extends SyslogServerIF> getSyslogServerClass() {
    return UDPSyslogServer.class;
  }

}
