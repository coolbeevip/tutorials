package com.coolbeevip.faker;

import com.coolbeevip.faker.core.Node;

public class Cpu extends Node {

  private final int count;
  public Cpu(Faker faker, int count) {
    super(faker);
    this.count = count;
  }

  /**
   * CPU 数量
   */
  public int count() {
    return this.count;
  }

  /**
   * 系统CPU使用率
   */
  public double usage(int min, int max) {
    return faker.randomPercentage(min, max);
  }

  /**
   * 1 分钟内 CPU 负载，包含的信息是在一段时间内CPU正在处理以及等待CPU处理的进程数之和的统计信息，也就是CPU使用队列的长度的统计信息。
   */
  public double loadAverage1m(int min, int max) {
    return faker.randomPercentage(min, max);
  }

  public Cpu low(){
    this.json.put("count",this.count);
    this.json.put("usage", usage(0,25));
    this.json.put("loadAverage1m", loadAverage1m(0,25));
    return this;
  }

  public Cpu mid(){
    this.json.put("count",this.count);
    this.json.put("usage", usage(25,50));
    this.json.put("loadAverage1m", loadAverage1m(25,50));
    return this;
  }

  public Cpu normal(){
    this.json.put("count",this.count);
    this.json.put("usage", usage(50,75));
    this.json.put("loadAverage1m", loadAverage1m(50,75));
    return this;
  }

  public Cpu high(){
    this.json.put("count",this.count);
    this.json.put("usage", usage(75,100));
    this.json.put("loadAverage1m", loadAverage1m(75,100));
    return this;
  }

}