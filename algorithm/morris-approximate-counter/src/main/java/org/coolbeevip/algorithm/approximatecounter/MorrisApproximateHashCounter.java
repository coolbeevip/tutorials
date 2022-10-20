package org.coolbeevip.algorithm.approximatecounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Random;
import java.util.UUID;

/**
 * 使用莫里斯计数实现对输入对象的统计
 *
 * 创建 size 大小的 byte 数组，对输入对象哈希后取余，使用余数作为数组索引，在索引位置累加1
 * 输入对象越多，size 越大，概率精度越高
 *
 * https://en.wikipedia.org/wiki/Approximate_counting_algorithm
 * https://www.matongxue.com/madocs/12/
 */
public class MorrisApproximateHashCounter {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private int size = 1000000;

  /**
   * 底数使用欧拉常数或者2
   */
  private double radix = 2.718;

  private byte[] exponents;

  /**
   * 使用随机模拟概率
   */
  Random random = new Random();

  public MorrisApproximateHashCounter(int size, int radix) {
    this.size = size;
    this.radix = radix;
    this.exponents = new byte[this.size];
  }


  /**
   * 计算计数器表示的近似值
   */
  public double get(Object value) {
    int index = getIndex(value);
    return Math.pow(this.radix,this.exponents[index]);
  }

  /**
   * 计数器累加
   */
  public void increment(Object value) {
    int index = getIndex(value);
    if(this.exponents[index] < 255 && random.nextDouble() < Math.pow(this.radix, -this.exponents[index])){
      this.exponents[index]++;
    }
    //log.debug("index {}, probability counter {}",index,this.exponents[index]);
  }

  /**
   * 通过散列获取给定项目的索引
   */
  private int getIndex(Object value) {
    //符号位去掉，保证结果只在正数区间
    int hash = value.hashCode() & Integer.MAX_VALUE;
    return hash % size;
  }

  public static void main(String[] args) {
    MorrisApproximateHashCounter mc = new MorrisApproximateHashCounter(2000, 2);

    String str = UUID.randomUUID().toString();
    int realCount = 200_000;
    for (int n = 0; n < realCount; n++) {
      mc.increment(str);
    }
    log.info("对象 {}, 实际计数 {}, 近似计数 {}", str, realCount, (int) mc.get(str));

    Integer num = 100;
    for (int n = 0; n < realCount; n++) {
      mc.increment(num);
    }
    log.info("对象 {}, 实际计数 {}, 近似计数 {}", num, realCount, (int) mc.get(num));
  }
}