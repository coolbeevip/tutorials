package com.coolbeevip.xml.cmdb.tree.format;

import com.coolbeevip.xml.cmdb.tree.Node;
import com.coolbeevip.xml.cmdb.tree.NodeFormatter;
import com.coolbeevip.xml.cmdb.tree.OperateType;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class PlantUmlActivityFormatter<T> implements NodeFormatter<T> {
  final OperateType operateType;

  public PlantUmlActivityFormatter(OperateType operateType) {
    this.operateType = operateType;
  }

  @Override
  public String format(Node<T> node) {
   Set<String> cacheSet = new HashSet<>();
    StringJoiner joiner = new StringJoiner("\n");

    joiner.add("@startuml");
    if(operateType==OperateType.DEPTH){
      node.depthFirstTraversal(n -> write(n, joiner, cacheSet));
    }else {
      node.breadthFirstTraversal(n -> write(n, joiner, cacheSet));
    }
    joiner.add("@enduml");

    return joiner.toString();
  }

  private void write(Node<T> n, StringJoiner joiner, Set<String> cacheSet){
    if (n.getParent() == null) {
      joiner.add("(*) --> " + quote(n.data.toString()));
    } else {
      String line = quote(n.getParent().data.toString()) + " --> " + quote(n.data.toString());
      if (!cacheSet.contains(line)) {
        joiner.add(line);
        cacheSet.add(line);
        if (n.isLeaf()) {
          String endLine = quote(n.data.toString()) + " --> (*)";
          if (!cacheSet.contains(endLine)) {
            joiner.add(endLine);
            cacheSet.add(endLine);
          }
        }
      }
    }
  }

  private String quote(String value){
    return "\""+value+"\"";
  }
}
