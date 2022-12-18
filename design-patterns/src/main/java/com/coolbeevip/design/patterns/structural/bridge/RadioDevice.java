package com.coolbeevip.design.patterns.structural.bridge;

public class RadioDevice implements Device {
  int vol;

  @Override
  public int getVol() {
    return vol;
  }

  @Override
  public int setVol(int vol) {
    this.vol = vol;
    return vol;
  }
}
