package com.coolbeevip.design.patterns.behavioral.iterator;

public interface ProfileIterator {
  boolean hasNext();

  Profile next();

  void reset();
}
