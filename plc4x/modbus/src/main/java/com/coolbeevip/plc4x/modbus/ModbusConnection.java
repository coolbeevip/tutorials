package com.coolbeevip.plc4x.modbus;

import lombok.extern.slf4j.Slf4j;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.model.PlcSubscriptionHandle;
import org.apache.plc4x.java.api.types.PlcResponseCode;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ModbusConnection implements AutoCloseable {

  private final PlcConnection plcConnection;
  private final String connectionString;

  public ModbusConnection(String connectionString) throws PlcConnectionException {
    this.connectionString = connectionString;
    this.plcConnection = new PlcDriverManager().getConnection(connectionString);
  }

  public void readData() throws ExecutionException, InterruptedException {
    if (!plcConnection.getMetadata().canRead()) {
      log.error("This connection doesn't support reading.");
      return;
    } else {
      // Create a new read request:
      // - Give the single item requested the alias name "value"
      PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
      builder.addItem("value-1", "coil:1");
      builder.addItem("value-2", "coil:2");
      builder.addItem("value-3", "coil:3[4]");
      builder.addItem("value-4", "holding-register:1");
      builder.addItem("value-5", "holding-register:3[4]");
      PlcReadRequest readRequest = builder.build();

      CompletableFuture<? extends PlcReadResponse> asyncResponse = readRequest.execute();
      asyncResponse.whenComplete((response, throwable) -> {
        // process the response
        if (throwable != null) {
          log.error("Error reading data", throwable);
        } else {
          log.info("Read data: {}", response);
          for (String fieldName : response.getFieldNames()) {
            if (response.getResponseCode(fieldName) == PlcResponseCode.OK) {
              int numValues = response.getNumberOfValues(fieldName);
              // If it's just one element, output just one single line.
              if (numValues == 1) {
                log.info("Value[" + fieldName + "]: " + response.getObject(fieldName));
              }
              // If it's more than one element, output each in a single row.
              else {
                log.info("Value[" + fieldName + "]:");
                for (int i = 0; i < numValues; i++) {
                  log.info(" - " + response.getObject(fieldName, i));
                }
              }
            }
            // Something went wrong, to output an error message instead.
            else {
              log.error("Error[" + fieldName + "]: " + response.getResponseCode(fieldName).name());
            }
          }
        }
      }).get();
    }
  }

  public void writeData() {
    if (!plcConnection.getMetadata().canWrite()) {
      log.error("This connection doesn't support writing.");
      return;
    } else {
      // Create a new write request:
      // - Give the single item requested the alias name "value"
      PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();
      builder.addItem("value-1", "coil:1", true);
      builder.addItem("value-2", "coil:3[4]", new boolean[]{true, false, true, false});
      builder.addItem("value-3", "holding-register:1", 1);
      builder.addItem("value-4", "holding-register:3[4]", new short[]{1, 2, 3, 4});
      PlcWriteRequest writeRequest = builder.build();

      CompletableFuture<? extends PlcWriteResponse> asyncResponse = writeRequest.execute();
      asyncResponse.whenComplete((response, throwable) -> {
        // process the response
        if (throwable != null) {
          log.error("Error writing data", throwable);
        } else {
          log.info("Write data: {}", response);
          for (String fieldName : response.getFieldNames()) {
            if (response.getResponseCode(fieldName) == PlcResponseCode.OK) {
              log.info("Value[" + fieldName + "]: " + response.getResponseCode(fieldName).name());
            }
            // Something went wrong, to output an error message instead.
            else {
              log.error("Error[" + fieldName + "]: " + response.getResponseCode(fieldName).name());
            }
          }
        }
      });
    }
  }

  public void subscribingData() {
    if (!plcConnection.getMetadata().canSubscribe()) {
      log.error("This connection doesn't support subscribing.");
      return;
    } else {
      PlcSubscriptionRequest.Builder builder = plcConnection.subscriptionRequestBuilder();
      builder.addChangeOfStateField("coil-1", "coil:1");
      builder.addCyclicField("holding-register-1", "holding-register:1", Duration.ofMillis(1000));
      PlcSubscriptionRequest subscriptionRequest = builder.build();

      CompletableFuture<? extends PlcSubscriptionResponse> asyncResponse = subscriptionRequest.execute();
      asyncResponse.whenComplete((response, throwable) -> {
        // process the response
        if (throwable != null) {
          log.error("Error subscribing data", throwable);
        } else {
          for (String subscriptionName : response.getFieldNames()) {
            final PlcSubscriptionHandle subscriptionHandle = response.getSubscriptionHandle(subscriptionName);
            subscriptionHandle.register(plcSubscriptionEvent -> {
              for (String fieldName : plcSubscriptionEvent.getFieldNames()) {
                log.info("{}={}", fieldName, plcSubscriptionEvent.getPlcValue(fieldName));
              }
            });
          }
        }
      });
    }
  }

  @Override
  public void close() throws Exception {
    if (plcConnection.isConnected()) {
      plcConnection.close();
    }
  }
}
