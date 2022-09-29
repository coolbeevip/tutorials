package com.coolbeevip.xml.cmdb.tree;

@FunctionalInterface
public interface Handler<T> {
  public void doHandler(Node<T> node);
}
