package org.coolbeevip.algorithm.approximatecounter;

import java.lang.invoke.MethodHandles;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 莫里斯近似计数算法
 * 使用一个 byte（数据范围 -128～127） 变量，实现千万级计数的近似计算
 *
 * https://en.wikipedia.org/wiki/Approximate_counting_algorithm
 * https://www.matongxue.com/madocs/12/
 */
public class MorrisApproximateCounter {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * 定义一个计数器变量
   */
  byte counter = 0;

  /**
   * 使用随机模拟概率
   */
  Random random = new Random();

  /**
   * 计算计数器表示的近似值
   */
  public double get() {
    return Math.exp(counter);
  }

  /**
   * 计数器累加
   */
  public void increment() {
    double probability = 1.0 / this.get();
    // 使用伪随机数增加概率
    if (random.nextDouble() < probability) {
      this.counter++;
    }
  }

  public static void main(String[] args) {
    MorrisApproximateCounter mc = new MorrisApproximateCounter();

    // 定义实际数量
    int realCount = 20_000_000;

    for (int n = 0; n < realCount; n++) {
      // 累加计数
      mc.increment();
    }

    // 输出实际计数 和 近似计数
    log.info("实际计数 {}, 近似计数 {}", realCount, (int) mc.get());
  }
}