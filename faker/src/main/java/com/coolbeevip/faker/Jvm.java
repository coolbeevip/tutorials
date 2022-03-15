package com.coolbeevip.faker;

import static com.coolbeevip.faker.core.Constants.JVM_MEMORY_MAX_KEY;
import static com.coolbeevip.faker.core.Constants.JVM_MEMORY_USED_KEY;
import static com.coolbeevip.faker.core.Constants.JVM_THREADS_DAEMON_KEY;
import static com.coolbeevip.faker.core.Constants.JVM_THREADS_LIVE_KEY;
import static com.coolbeevip.faker.core.Constants.JVM_THREADS_PEAK_KEY;

import com.coolbeevip.faker.core.Node;
import java.math.BigInteger;

public class Jvm extends Node {

  /**
   * 最大内存
   */
  private final BigInteger memoryMax;


  public Jvm(Faker faker, BigInteger memoryMax) {
    super(faker);
    this.memoryMax = memoryMax;
  }

  /**
   * 年轻代 GC 耗时
   */
  public long gcYoungTime(long min, long max) {
    return faker.randomLong(min, max);
  }

  /**
   * 年老代 GC 耗时
   */
  public long gcOldTime(long min, long max) {
    return faker.randomLong(min, max);
  }

  public Jvm low() {
    this.json.put(JVM_MEMORY_MAX_KEY, this.memoryMax);
    BigInteger min = this.memoryMax.divide(BigInteger.valueOf(100)).multiply(BigInteger.valueOf(1));
    BigInteger max = this.memoryMax.divide(BigInteger.valueOf(100))
        .multiply(BigInteger.valueOf(25));
    BigInteger memoryUsed = faker.randomBigInteger(min, max);
    this.json.put(JVM_MEMORY_USED_KEY, memoryUsed);
    this.json.put(JVM_THREADS_DAEMON_KEY, faker.randomLong(0, 25));
    this.json.put(JVM_THREADS_LIVE_KEY, faker.randomLong(0, 25));
    this.json.put(JVM_THREADS_PEAK_KEY, faker.randomLong(23, 25));
    return this;
  }

  public Jvm mid() {
    this.json.put(JVM_MEMORY_MAX_KEY, this.memoryMax);
    BigInteger min = this.memoryMax.divide(BigInteger.valueOf(100))
        .multiply(BigInteger.valueOf(25));
    BigInteger max = this.memoryMax.divide(BigInteger.valueOf(100))
        .multiply(BigInteger.valueOf(50));
    BigInteger memoryUsed = faker.randomBigInteger(min, max);
    this.json.put(JVM_MEMORY_USED_KEY, memoryUsed);
    this.json.put(JVM_THREADS_DAEMON_KEY, faker.randomLong(25, 50));
    this.json.put(JVM_THREADS_LIVE_KEY, faker.randomLong(25, 50));
    this.json.put(JVM_THREADS_PEAK_KEY, faker.randomLong(25, 50));
    return this;
  }

  public Jvm normal() {
    this.json.put(JVM_MEMORY_MAX_KEY, this.memoryMax);
    BigInteger min = this.memoryMax.divide(BigInteger.valueOf(100))
        .multiply(BigInteger.valueOf(50));
    BigInteger max = this.memoryMax.divide(BigInteger.valueOf(100))
        .multiply(BigInteger.valueOf(75));
    BigInteger memoryUsed = faker.randomBigInteger(min, max);
    this.json.put(JVM_MEMORY_USED_KEY, memoryUsed);
    this.json.put(JVM_THREADS_DAEMON_KEY, faker.randomLong(50, 75));
    this.json.put(JVM_THREADS_LIVE_KEY, faker.randomLong(50, 75));
    this.json.put(JVM_THREADS_PEAK_KEY, faker.randomLong(50, 75));
    return this;
  }

  public Jvm high() {
    this.json.put(JVM_MEMORY_MAX_KEY, this.memoryMax);
    BigInteger min = this.memoryMax.divide(BigInteger.valueOf(100))
        .multiply(BigInteger.valueOf(75));
    BigInteger max = this.memoryMax.divide(BigInteger.valueOf(100))
        .multiply(BigInteger.valueOf(100));
    BigInteger memoryUsed = faker.randomBigInteger(min, max);
    this.json.put(JVM_MEMORY_USED_KEY, memoryUsed);
    this.json.put(JVM_THREADS_DAEMON_KEY, faker.randomLong(75, 100));
    this.json.put(JVM_THREADS_LIVE_KEY, faker.randomLong(75, 100));
    this.json.put(JVM_THREADS_PEAK_KEY, faker.randomLong(75, 100));
    return this;
  }
}