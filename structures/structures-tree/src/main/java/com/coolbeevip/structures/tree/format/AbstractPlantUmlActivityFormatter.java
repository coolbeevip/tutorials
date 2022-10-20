package com.coolbeevip.structures.tree.format;

import com.coolbeevip.structures.tree.Node;
import com.coolbeevip.structures.tree.NodeFormatter;
import com.coolbeevip.structures.tree.OperateType;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public abstract class AbstractPlantUmlActivityFormatter<T> implements NodeFormatter<T> {
  final OperateType operateType;

  protected abstract String getName(T value);

  public AbstractPlantUmlActivityFormatter(OperateType operateType) {
    this.operateType = operateType;
  }

  @Override
  public String format(Node<T> node) {
    Set<String> cacheSet = new HashSet<>();
    StringJoiner joiner = new StringJoiner("\n");

    joiner.add("@startuml");
    if (operateType == OperateType.DEPTH) {
      node.depthFirstTraversal(n -> write(n, joiner, cacheSet));
    } else {
      node.breadthFirstTraversal(n -> write(n, joiner, cacheSet));
    }
    joiner.add("@enduml");

    return joiner.toString();
  }

  private void write(Node<T> n, StringJoiner joiner, Set<String> cacheSet) {
    if (n.getParent() == null) {
      joiner.add("(*) --> " + quote(n.data));
    } else {
      String line = quote(n.getParent().data) + " --> " + quote(n.data);
      if (!cacheSet.contains(line)) {
        joiner.add(line);
        cacheSet.add(line);
        if (n.isLeaf()) {
          String endLine = quote(n.data) + " --> (*)";
          if (!cacheSet.contains(endLine)) {
            joiner.add(endLine);
            cacheSet.add(endLine);
          }
        }
      }
    }
  }

  private String quote(T value) {
    return "\"" + getName(value) + "\"";
  }
}
