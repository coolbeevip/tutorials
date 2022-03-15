package com.coolbeevip.faker;

import com.coolbeevip.faker.core.Constants.RiskLevel;
import com.coolbeevip.faker.core.Node;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigInteger;

public class Proc extends Node {

  private final String name;
  private final Host host;
  private final Jvm jvm;
  private final long powerOnTime;
  private final long filesMax;

  public Proc(Faker faker, String name, long filesMax, BigInteger jvmMemoryMax, Host host) {
    super(faker);
    this.name = name;
    this.host = host;
    this.powerOnTime = System.currentTimeMillis();
    this.filesMax = filesMax;
    this.jvm = new Jvm(faker, jvmMemoryMax);
  }

  public Proc take(RiskLevel riskLevel) {
    this.json.put("name",this.name);
    this.json.put("uptime",System.currentTimeMillis() - this.powerOnTime);
    this.json.put("files.max",this.filesMax);
    if (riskLevel == RiskLevel.LOW) {
      this.json.put("usage",faker.randomPercentage(0, 25));
      this.json.put("files.open",faker.randomLong(5,(long)(this.filesMax*0.25)));
      this.json.set("jvm", this.jvm.low().getJson());
    } else if (riskLevel == RiskLevel.MID) {
      this.json.put("usage",faker.randomPercentage(25, 50));
      this.json.put("files.open",faker.randomLong(5,(long)(this.filesMax*0.5)));
      this.json.set("jvm", this.jvm.mid().getJson());
    } else if (riskLevel == RiskLevel.NORMAL) {
      this.json.put("usage",faker.randomPercentage(50, 75));
      this.json.put("files.open",faker.randomLong(5,(long)(this.filesMax*0.75)));
      this.json.set("jvm", this.jvm.normal().getJson());
    } else if (riskLevel == RiskLevel.HIGH) {
      this.json.put("usage",faker.randomPercentage(75, 100));
      this.json.put("files.open",faker.randomLong(5,(long)(this.filesMax)));
      this.json.set("jvm", this.jvm.high().getJson());
    }
    ObjectNode relationships = mapper.createObjectNode();
    relationships.put("hosted-on",this.host.getId());
    this.json.set("relationships",relationships);
    return this;
  }
}