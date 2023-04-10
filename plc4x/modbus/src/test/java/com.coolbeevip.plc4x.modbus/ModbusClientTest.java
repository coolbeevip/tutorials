package com.coolbeevip.plc4x.modbus;

import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

@Disabled
public class ModbusClientTest {

  @Test
  public void testReadData() throws PlcConnectionException, ExecutionException, InterruptedException {
    ModbusClient connection = new ModbusClient("modbus-tcp://localhost:502");
    connection.readData();
  }
}
