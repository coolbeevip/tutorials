package com.coolbeevip.design.patterns.structural.bridge;

public class Remote {
  protected final Device device;

  public Remote(Device device) {
    this.device = device;
  }

  public int volUp() {
    return this.device.setVol(this.device.getVol() + 1);
  }

  public int volDown() {
    return this.device.setVol(this.device.getVol() - 1);
  }
}
