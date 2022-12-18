package com.coolbeevip.design.patterns.structural.bridge;

public class AdvancedRemote extends Remote {

  public AdvancedRemote(Device device) {
    super(device);
  }

  public int setVolUp(int vol) {
    return this.device.setVol(vol);
  }
}
