package org.coolbeevip.syslog.udp;

import lombok.extern.slf4j.Slf4j;
import org.coolbeevip.syslog.ReceiveEventHandler;
import org.coolbeevip.syslog.Rfc5424SyslogEvent;
import org.coolbeevip.syslog.SyslogService;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.impl.net.udp.UDPNetSyslogServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

@Slf4j
public class UDPSyslogServer extends UDPNetSyslogServer {
  private ReceiveEventHandler handler;

  @Override
  public void shutdown() {
    super.shutdown();
    thread = null;
  }

  @Override
  public void run() {
    handler = SyslogService.getReceiveEventHandler(this.syslogServerConfig.getHost(), this.syslogServerConfig.getPort());
    this.shutdown = false;
    try {
      this.ds = createDatagramSocket();
    } catch (Exception e) {
      System.err.println("Creating DatagramSocket failed");
      e.printStackTrace();
      throw new SyslogRuntimeException(e);
    }

    byte[] receiveData = new byte[SyslogConstants.SYSLOG_BUFFER_SIZE];

    while (!this.shutdown) {
      try {
        final DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);
        this.ds.receive(dp);
        final Rfc5424SyslogEvent event = new Rfc5424SyslogEvent(receiveData, dp.getOffset(), dp.getLength());
        handler.receive(event);
        log.debug("Syslog message {}", event);
      } catch (SocketException se) {
        se.printStackTrace();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }
}
