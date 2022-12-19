package com.coolbeevip.design.patterns.structural.flyweight;

import java.util.ArrayList;
import java.util.List;

public class Forest {
  private List<Tree> trees = new ArrayList<>();

  public Tree plantTree(int x, int y, String name, String color, byte[] texture) {
    TreeType type = TreeFactory.getTreeType(name, color, texture);
    Tree tree = new Tree(type, x, y);
    trees.add(tree);
    return tree;
  }

  public void draw() {
    trees.stream().forEach(tree -> tree.draw());
  }
}
