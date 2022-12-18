package com.coolbeevip.design.patterns.structural.bridge;

public class TVDevice implements Device {
  int vol;

  @Override
  public int getVol() {
    return this.vol;
  }

  @Override
  public int setVol(int vol) {
    this.vol = vol;
    return this.vol;
  }
}
