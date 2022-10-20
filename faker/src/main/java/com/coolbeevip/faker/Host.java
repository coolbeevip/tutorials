package com.coolbeevip.faker;

import com.coolbeevip.faker.core.Constants.RiskLevel;
import com.coolbeevip.faker.core.Node;

import java.util.ArrayList;
import java.util.List;

public class Host extends Node {

  private final String ip;
  private final Cpu cpu;
  private final Disk disk;
  private final Memory memory;
  private final List<Proc> processes = new ArrayList<>();

  public Host(Faker faker, String ip, int cpuNum, long diskTotalSpace, long memoryTotal) {
    super(faker);
    this.ip = ip;
    this.cpu = new Cpu(faker, cpuNum);
    this.disk = new Disk(faker, diskTotalSpace);
    this.memory = new Memory(faker, memoryTotal);
  }

  public void deployProcess(Proc process) {
    this.processes.add(process);
  }

  public Cpu getCpu() {
    return cpu;
  }

  public Disk getDisk() {
    return disk;
  }

  public Memory getMemory() {
    return memory;
  }

  public Host take(RiskLevel riskLevel) {
    this.json.put("ip", this.ip);
    if (riskLevel == RiskLevel.LOW) {
      this.json.set("cpu", this.getCpu().low().getJson());
      this.json.set("disk", this.getDisk().low().getJson());
      this.json.set("memory", this.getMemory().low().getJson());
    } else if (riskLevel == RiskLevel.MID) {
      this.json.set("cpu", this.getCpu().mid().getJson());
      this.json.set("disk", this.getDisk().mid().getJson());
      this.json.set("memory", this.getMemory().mid().getJson());
    } else if (riskLevel == RiskLevel.NORMAL) {
      this.json.set("cpu", this.getCpu().normal().getJson());
      this.json.set("disk", this.getDisk().normal().getJson());
      this.json.set("memory", this.getMemory().normal().getJson());
    } else if (riskLevel == RiskLevel.HIGH) {
      this.json.set("cpu", this.getCpu().high().getJson());
      this.json.set("disk", this.getDisk().high().getJson());
      this.json.set("memory", this.getMemory().high().getJson());
    }
    return this;
  }
}