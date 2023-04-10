package com.coolbeevip.plc4x.opcua;

import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.junit.jupiter.api.Test;

public class OPCUAClientTest {

  @Test
  public void test() throws PlcConnectionException {
    OPCUAClient client = new OPCUAClient("opcua:tcp://127.0.0.1:12686?discovery=true&username=admin&password=password");
  }
}
