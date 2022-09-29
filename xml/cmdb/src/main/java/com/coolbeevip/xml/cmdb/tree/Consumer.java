package com.coolbeevip.xml.cmdb.tree;

@FunctionalInterface
public interface Consumer<T> {
  void accept(Node<T> node);
}
