package com.coolbeevip.ratis.sequence;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

@Slf4j
public class SequenceTest {

  static String peerAddress = "127.0.0.1:9001,127.0.0.1:9002,127.0.0.1:9003";
  static List<SequenceServer> servers = new ArrayList<>();

  @BeforeAll
  public static void setup() throws ExecutionException, InterruptedException {

    Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(Level.INFO);

    Arrays.stream(peerAddress.split(",")).forEach(addr -> {
      servers.add(SequenceServer.builder()
          .address(addr)
          .peerAddress(Arrays.asList(peerAddress.split(",")))
          .build());
    });

    List<CompletableFuture<Void>> futures = new ArrayList<>();
    servers.stream().parallel().forEach(s -> {
      try {
        futures.add(s.start());
      } catch (IOException e) {
        //
      }
    });

    CompletableFuture<Void> combinedFuture = CompletableFuture
        .allOf(futures.toArray(new CompletableFuture[futures.size()]));
    combinedFuture.get();

  }

  @AfterAll
  @SneakyThrows
  public static void tearDown() {
    servers.stream().parallel().forEach(s -> {
      try {
        s.stop();
      } catch (IOException e) {
        //
      }
    });
  }

  @Test
  @SneakyThrows
  public void test() {
    Sequence sequence = Sequence.builder().peerAddress(Arrays.asList(peerAddress.split(",")))
        .build();
    CompletableFuture<Void> future = sequence.start();
    future.join();
    //Thread.sleep(600000);
    for (int i = 0; i < 100; i++) {
      System.out.println(">>>>>>>>>>" + sequence.genericId());
    }

  }
}
