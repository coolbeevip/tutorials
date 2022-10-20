package com.coolbeevip.structures.tree;

@FunctionalInterface
public interface Predicate<T> {
  boolean test(Node<T> node);
}
