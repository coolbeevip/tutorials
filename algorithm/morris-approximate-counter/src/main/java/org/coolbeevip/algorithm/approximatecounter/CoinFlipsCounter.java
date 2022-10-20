package org.coolbeevip.algorithm.approximatecounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Random;

/**
 * 抛硬币近似计数
 */

public class CoinFlipsCounter {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  int counters;

  /**
   * 模拟抛硬币概率
   */
  Random random = new Random();

  public void increment() {
    if (random.nextDouble() > 0.5) {
      counters++;
    }
  }

  public int get() {
    return counters;
  }

  public static void main(String[] args) {
    CoinFlipsCounter cm = new CoinFlipsCounter();
    int actualCount = 400;
    for (int i = 0; i < actualCount; i++) {
      cm.increment();
    }
    log.info("实际计数 {}, 近似计数 {}", actualCount, cm.get() * 2);
  }
}