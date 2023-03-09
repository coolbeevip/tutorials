package com.coolbeevip.design.patterns.structural.flyweight;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建享元对象并缓存
 */
public class TreeFactory {
  private static Map<String, TreeType> treeTypes = new HashMap<>();

  public static TreeType getTreeType(String name, String color, byte[] texture) {
    if (treeTypes.containsKey(name)) {
      return treeTypes.get(name);
    } else {
      TreeType type = new TreeType(name, color, texture);
      treeTypes.put(type.getName(), type);
      return type;
    }
  }
}
