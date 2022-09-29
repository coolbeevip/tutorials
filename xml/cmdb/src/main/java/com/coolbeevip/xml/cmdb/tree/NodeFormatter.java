package com.coolbeevip.xml.cmdb.tree;

public interface NodeFormatter<T> {
  String format(Node<T> node);
}
