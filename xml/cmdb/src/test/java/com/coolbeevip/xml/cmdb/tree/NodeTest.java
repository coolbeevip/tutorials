package com.coolbeevip.xml.cmdb.tree;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Iterator;

@Slf4j
public class NodeTest {

  @Test
  public void initTest() {
    Node tree = genTree();
    // 深度优先
    tree.depthFirstTraversal(node -> {
      System.out.println(createIndent(node.getLevel()) + " " + node.data);
    });
    // 广度优先
    tree.breadthFirstTraversal(node -> {
      System.out.println(createIndent(node.getLevel()) + " " + node.data);
    });
  }

  @Test
  public void removeIteratorTest() {
    Node tree = genTree();

    log.info("=== origin ====");
    tree.depthFirstTraversal(node -> {
      System.out.println(createIndent(node.getLevel()) + " " + node.data + " ");
    });

    Iterator<Node> it = tree.iterator();
    while (it.hasNext()) {
      Node node = it.next();
      if (node.data.equals("B1-1")) {
        it.remove();
      }
    }

    log.info("=== 删除后 ====");
    tree.depthFirstTraversal(node -> {
      System.out.println(createIndent(node.getLevel()) + " " + node.data + " ");
    });
  }

  @Test
  public void removeChildTest() {
    Node tree = genTree();

    tree.breadthFirstTraversal(node -> {
      if (node.data.equals("B1-1")) {
        node.getParent().removeChild(node);
      }
    });
    tree.depthFirstTraversal(node -> {
      System.out.println(createIndent(node.getLevel()) + " " + node.data + " ");
    });
  }

//  @Test
//  public void removeByIteratorTest() {
//    Node tree = genTree();
//    Iterator<Node> it = tree.iterator();
//    while (it.hasNext()) {
//      Node node = it.next();
//      if (node.data.equals("M2-1")) {
//        it.remove();
//      }
//    }
//    tree.depthFirstTraversal(node -> {
//      System.out.println(createIndent(node.getLevel()) + " " + node.data + " ");
//    });
//  }

  public Node<String> genTree() {
    Node tree = new Node("ROOT");
    Node a = tree.addChild("A");
    a.addChild("A1-1").addChild("A1-1-1");
    a.addChild("A1-2").addChild("A1-2-1");
    a.addChild("A1-3").addChild("A1-3-1");
    Node b = tree.addChild("B");
    b.addChild("B1-1").addChild("B1-1-1");
    b.addChild("B1-2").addChild("B1-2-1");
    b.addChild("B1-3").addChild("B1-3-1");
    return tree;
  }

  private String createIndent(int depth) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      sb.append(' ');
    }
    return sb.toString();
  }
}
