package com.coolbeevip.ratis.sequence;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SequenceTest extends TestKit {

  @Test
  @SneakyThrows
  public void test() {
    Sequence sequence = Sequence.builder().peerAddress(Arrays.asList(peerAddress.split(",")))
        .build();
    CompletableFuture<Void> future = sequence.start();
    future.join();

    for (int i = 0; i < 100; i++) {
      System.out.println(">>>>>>>>>>" + sequence.genericId());
    }

  }
}
