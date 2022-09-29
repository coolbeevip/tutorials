package com.coolbeevip.xml.cmdb.tree;

@FunctionalInterface
public interface Predicate<T> {
  boolean test(Node<T> node);
}
