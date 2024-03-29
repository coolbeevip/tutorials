package org.coolbeevip.syslog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.productivity.java.syslog4j.server.SyslogServerEventIF;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Rfc5424SyslogEvent implements SyslogServerEventIF {

  private static ObjectMapper mapper = new ObjectMapper();

  private static final long serialVersionUID = 1L;
  private static final char SP = ' ';
  private static final String CHARSET = "UTF-8";
  private static final String NIL = "-";
  private static final byte[] UTF_8_BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

  private final byte[] raw;

  private final String prioVersion;
  private final int facility;
  private final int level;
  private final int version;

  private final String timestamp;
  private final String host;
  private final String appName;
  private final String procId;
  private final String msgId;
  private final String structuredData;
  private final String message;

  public Rfc5424SyslogEvent(byte[] data, int offset, int length) {
    raw = new byte[length - offset];
    System.arraycopy(data, offset, raw, 0, length);
    int startPos = 0;
    int endPos = -1;

    endPos = searchChar(raw, startPos, SP);
    prioVersion = getString(raw, startPos, endPos);

    startPos = endPos + 1;
    endPos = searchChar(raw, startPos, ' ');
    timestamp = getString(raw, startPos, endPos);

    startPos = endPos + 1;
    endPos = searchChar(raw, startPos, ' ');
    host = getString(raw, startPos, endPos);

    startPos = endPos + 1;
    endPos = searchChar(raw, startPos, ' ');
    appName = getString(raw, startPos, endPos);

    startPos = endPos + 1;
    endPos = searchChar(raw, startPos, ' ');
    procId = getString(raw, startPos, endPos);

    startPos = endPos + 1;
    endPos = searchChar(raw, startPos, ' ');
    msgId = getString(raw, startPos, endPos);

    startPos = endPos + 1;
    if (raw[startPos] == '[') {
      endPos = searchChar(raw, startPos, ']') + 1;
    } else {
      endPos = searchChar(raw, startPos, ' ');
      if (endPos == -1)
        endPos = raw.length;
    }
    structuredData = getString(raw, startPos, endPos);

    startPos = endPos + 1;
    if (startPos < raw.length) {
      if (startPos + 3 < raw.length && raw[startPos] == UTF_8_BOM[0] && raw[startPos + 1] == UTF_8_BOM[1]
          && raw[startPos + 2] == UTF_8_BOM[2]) {
        startPos += 3;
      }
      message = getString(raw, startPos, raw.length);
    } else {
      message = null;
    }

    // parse priority and version
    endPos = prioVersion.indexOf(">");
    final String priorityStr = prioVersion.substring(1, endPos);
    int priority = 0;
    try {
      priority = Integer.parseInt(priorityStr);
    } catch (NumberFormatException nfe) {
      System.err.println("Can't parse priority");
    }

    level = priority & 7;
    facility = (priority - level) >> 3;

    startPos = endPos + 1;
    int ver = 0;
    if (startPos < prioVersion.length()) {
      try {
        ver = Integer.parseInt(prioVersion.substring(startPos));
      } catch (NumberFormatException nfe) {
        System.err.println("Can't parse version");
        ver = -1;
      }
    }
    version = ver;
  }

  private String getString(byte[] data, int startPos, int endPos) {
    try {
      return new String(data, startPos, endPos - startPos, CHARSET);
    } catch (UnsupportedEncodingException e) {
      System.err.println("Unsupported encoding");
    }
    return "";
  }

  /**
   * Try to find a character in given byte array, starting from startPos.
   *
   * @param data
   * @param startPos
   * @param c
   * @return position of the character or -1 if not found
   */
  private int searchChar(byte[] data, int startPos, char c) {
    for (int i = startPos; i < data.length; i++) {
      if (data[i] == c) {
        return i;
      }
    }
    return -1;
  }

  public String getPrioVersion() {
    return prioVersion;
  }

  public int getFacility() {
    return facility;
  }

  public int getLevel() {
    return level;
  }

  public int getVersion() {
    return version;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public String getHost() {
    return host;
  }

  public String getAppName() {
    return appName;
  }

  public String getProcId() {
    return procId;
  }

  public String getMsgId() {
    return msgId;
  }

  public String getStructuredData() {
    return structuredData;
  }

  public String getMessage() {
    return message;
  }

  public String getCharSet() {
    return CHARSET;
  }

  public byte[] getRaw() {
    return raw;
  }

  public Date getDate() {
    if (NIL.equals(timestamp)) {
      return null;
    }
    String fixTz = timestamp.replace("Z", "+00:00");
    final int tzSeparatorPos = fixTz.lastIndexOf(":");
    fixTz = fixTz.substring(0, tzSeparatorPos) + fixTz.substring(tzSeparatorPos + 1);
    try {
      return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ").parse(fixTz);
    } catch (ParseException e) {
      System.err.println("Unable to parse date " + timestamp);
    }
    return null;
  }

  public void setCharSet(String charSet) {
  }

  public void setFacility(int facility) {
  }

  public void setDate(Date date) {
  }

  public void setLevel(int level) {
  }

  public void setHost(String host) {
  }

  public void setMessage(String message) {
  }

  @Override
  public String toString() {
    return "Rfc5424SyslogEvent [prioVersion=" + prioVersion + ", facility=" + facility + ", level=" + level + ", version="
        + version + ", timestamp=" + timestamp + ", host=" + host + ", appName=" + appName + ", procId=" + procId
        + ", msgId=" + msgId + ", structuredData=" + structuredData + ", message=" + message + "]";
  }

  public String toJSON() {
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
