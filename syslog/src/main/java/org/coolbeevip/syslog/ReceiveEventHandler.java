package org.coolbeevip.syslog;

@FunctionalInterface
public interface ReceiveEventHandler {
  void receive(Rfc5424SyslogEvent event);
}
