package com.coolbeevip.ignite.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteException;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;

/**
 * BEFORE_NODE_START：Ignite节点的启动程序初始化之前调用
 * AFTER_NODE_START：Ignite节点启动之后调用
 * BEFORE_NODE_STOP：Ignite节点的停止程序初始化之前调用
 * AFTER_NODE_STOP：Ignite节点停止之后调用
 */
@Slf4j
public class IgniteNodeLifecycle implements LifecycleBean {

  @Override
  public void onLifecycleEvent(LifecycleEventType lifecycleEventType) throws IgniteException {
    log.debug("lifecycle {}",lifecycleEventType.name());
  }
}