package com.coolbeevip.ratis.sequence.benchmarks;

import com.coolbeevip.ratis.sequence.SequenceServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public abstract class ClusterKit {

  public static List<String> peerAddress = Arrays.asList(
      "127.0.0.1:40062",// + TutorialsTestSuite.getInstance().findAvailableTcpPort(),
      "127.0.0.1:40750",// + TutorialsTestSuite.getInstance().findAvailableTcpPort(),
      "127.0.0.1:40822");// + TutorialsTestSuite.getInstance().findAvailableTcpPort());

  public static List<SequenceServer> servers = new ArrayList<>();

  public static void startClusters() throws ExecutionException, InterruptedException {

    log.info("开始启动集群");
    peerAddress.stream().forEach(addr -> {
      servers.add(SequenceServer.builder()
          .address(addr)
          .peerAddress(peerAddress)
          .build());
    });

    List<CompletableFuture<Void>> futures = new ArrayList<>();
    servers.stream().parallel().forEach(s -> {
      try {
        futures.add(s.start());
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    });

    CompletableFuture<Void> combinedFuture = CompletableFuture
        .allOf(futures.toArray(new CompletableFuture[futures.size()]));
    combinedFuture.get();

  }

  @SneakyThrows
  public static void stopClusters() {
    servers.stream().forEach(SequenceServer::stop);
  }
}