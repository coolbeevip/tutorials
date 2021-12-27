package com.coolbeevip.ignite.persistence.storage.rdbms;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.ClientConfiguration;

public class IgniteClient {

  public static void main(String[] args) throws Exception {
    ClientConfiguration clientConfiguration = new ClientConfiguration()
        .setUserName("ignite")
        .setUserPassword("ignite")
        .setAddresses("10.1.207.180:10800","10.1.207.181:10800","10.1.207.182:10800");
    try (org.apache.ignite.client.IgniteClient client = Ignition.startClient(clientConfiguration)) {

    }
  }
}