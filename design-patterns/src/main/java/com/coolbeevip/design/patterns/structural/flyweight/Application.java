package com.coolbeevip.design.patterns.structural.flyweight;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

import java.io.IOException;

public class Application {
  public static void main(String[] args) throws IOException {
    goodForest();
    //bedForest();
  }

  /**
   * 使用享元模式
   * 创建了 100 万个 tree 对象，和 1 个 treeType 对象
   * 共占用 28864200 byte (28.86 MB)
   */
  private static void goodForest() {
    Forest forest = new Forest();
    for (int i = 0; i < 1000000; i++) {
      Tree tree = forest.plantTree(i, i + 1, "松树", "green", new byte[2048]);
      if (i == 0) {
        System.out.println(ClassLayout.parseClass(BedTree.class).toPrintable());
        System.out.println(ClassLayout.parseInstance(tree).toPrintable());
        System.out.println(GraphLayout.parseInstance(tree).toFootprint());
        System.out.println(GraphLayout.parseInstance(tree).totalSize());
      }
    }
    // forest.draw();
    System.out.println(GraphLayout.parseInstance(forest).totalSize());
  }

  /**
   * 未使用享元模式
   * 创建了 100 万个 bed tree
   * 共占用 4148862112 byte(4.148 GB)
   */
  private static void bedForest() {
    BedForest forest = new BedForest();
    for (int i = 0; i < 1000000; i++) {
      BedTree bedTree = forest.plantTree(i, i + 1, "松树", "green", new byte[4096]);
      if (i == 0) {
        System.out.println(ClassLayout.parseClass(BedTree.class).toPrintable());
        System.out.println(ClassLayout.parseInstance(bedTree).toPrintable());
        System.out.println(GraphLayout.parseInstance(bedTree).toFootprint());
        System.out.println(GraphLayout.parseInstance(bedTree).totalSize());
      }
    }
    //forest.draw();
    System.out.println(GraphLayout.parseInstance(forest).totalSize());
  }
}
