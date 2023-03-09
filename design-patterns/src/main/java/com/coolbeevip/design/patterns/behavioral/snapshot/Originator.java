package com.coolbeevip.design.patterns.behavioral.snapshot;

/**
 * 原发器，状态对象
 */
public interface Originator {
  Snapshot createSnapshot();
}
