package com.coolbeevip.plc4x.opcua;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;

public class OPCUAClient {
  private final PlcConnection plcConnection;
  private final String connectionString;

  public OPCUAClient(String connectionString) throws PlcConnectionException {
    this.connectionString = connectionString;
    this.plcConnection = new PlcDriverManager().getConnection(connectionString);
  }
}
