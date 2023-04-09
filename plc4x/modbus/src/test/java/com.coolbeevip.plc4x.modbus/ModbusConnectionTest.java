package com.coolbeevip.plc4x.modbus;

import org.apache.plc4x.java.api.exceptions.PlcConnectionException;

import java.util.concurrent.ExecutionException;

public class ModbusConnectionTest {

  //@Test
  public void testReadData() throws PlcConnectionException, ExecutionException, InterruptedException {
    ModbusConnection connection = new ModbusConnection("modbus-tcp://localhost:502");
    connection.readData();
  }

  //@Test
  public void testSubscribeData() throws PlcConnectionException, InterruptedException {
    ModbusConnection connection = new ModbusConnection("modbus-tcp://localhost:502");
    connection.subscribingData();
    Thread.sleep(60000);
  }
}
