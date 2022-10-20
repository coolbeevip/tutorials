package com.coolbeevip.structures.tree;

@FunctionalInterface
public interface Consumer<T> {
  void accept(Node<T> node);
}
