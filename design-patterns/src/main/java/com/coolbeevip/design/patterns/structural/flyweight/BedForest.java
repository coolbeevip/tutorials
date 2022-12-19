package com.coolbeevip.design.patterns.structural.flyweight;

import java.util.ArrayList;
import java.util.List;

public class BedForest {
  private List<BedTree> trees = new ArrayList<>();

  public BedTree plantTree(int x, int y, String name, String color, byte[] texture) {
    BedTree tree = new BedTree(name, color, texture, x, y);
    trees.add(tree);
    return tree;
  }

  public void draw() {
    trees.stream().forEach(tree -> tree.draw());
  }
}
