package com.coolbeevip.ratis.sequence;

import com.coolbeevip.ratis.sequence.benchmarks.ClusterKit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class SequenceClientTest extends ClusterKit {

  @BeforeClass
  public static void setup() throws ExecutionException, InterruptedException {
    startClusters();
  }

  @AfterClass
  public static void tearDown() {
    stopClusters();
  }

  @Test
  @SneakyThrows
  public void test() {
    SequenceClient sequence = SequenceClient.builder().peerAddress(peerAddress)
        .build();
    CompletableFuture<Void> future = sequence.start();
    future.join();

    String prevValue = null;
    for (int i = 0; i < 100; i++) {
      String value = sequence.genericId();
      if (prevValue != null) {
        assertThat(Integer.parseInt(value) - Integer.parseInt(prevValue), Matchers.is(1));
        prevValue = value;
      }
    }

  }
}
