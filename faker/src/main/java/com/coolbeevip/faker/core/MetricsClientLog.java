package com.coolbeevip.faker.core;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricsClientLog implements MetricsClient {

  @Override
  public void push(String json) {
    log.info(json);
  }
}