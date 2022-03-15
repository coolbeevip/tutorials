package com.coolbeevip.faker;

import com.coolbeevip.faker.core.Node;

public class Memory extends Node {

  private final long total;

  public Memory(Faker faker, long total) {
    super(faker);
    this.total = total;
  }

  /**
   * 剩余磁盘空间
   */
  public long freeSpace(long min){
    return faker.randomLong(min, total);
  }

  public Memory low(){
    this.json.put("total",this.total);
    this.json.put("free", freeSpace((long)(this.total * 0.9)));
    return this;
  }

  public Memory mid(){
    this.json.put("total",this.total);
    this.json.put("free", freeSpace((long)(this.total * 0.5)));
    return this;
  }

  public Memory normal(){
    this.json.put("total",this.total);
    this.json.put("free", freeSpace((long)(this.total * 0.3)));
    return this;
  }

  public Memory high(){
    this.json.put("total",this.total);
    this.json.put("free", freeSpace((long)(this.total * 0.1)));
    return this;
  }
}