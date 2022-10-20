package com.coolbeevip.structures.tree;

import com.coolbeevip.structures.tree.format.DefaultPlantUmlActivityFormatter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class NodeTest {

  @Test
  @SneakyThrows
  public void depthFormatTest() {
    Node<String> tree = genTree();
    NodeFormatter formatter = new DefaultPlantUmlActivityFormatter(OperateType.DEPTH);
    String text = tree.writeValueAsString(formatter);
    log.info("{}", text);
    try (FileWriter fileWriter = new FileWriter(Paths.get("target/depth-activity.puml").toFile());
         PrintWriter printWriter = new PrintWriter(fileWriter)) {
      printWriter.print(text);
    }
  }

  @Test
  @SneakyThrows
  public void breadthFormatTest() {
    Node<String> tree = genTree();
    NodeFormatter formatter = new DefaultPlantUmlActivityFormatter(OperateType.BREADTH);
    String text = tree.writeValueAsString(formatter);
    log.info("{}", text);
    try (FileWriter fileWriter = new FileWriter(Paths.get("target/breadth-activity.puml").toFile());
         PrintWriter printWriter = new PrintWriter(fileWriter)) {
      printWriter.print(text);
    }
  }

  /**
   * 深度优先遍历
   */
  @Test
  public void depthFirstTraversalTest() {
    Node<String> tree = genTree();
    List<String> list = new LinkedList<>();
    tree.depthFirstTraversal(node -> list.add(node.data));
    assertThat(tree.getChildren().size(), Matchers.is(2));
    log.info("{}", tree.writeDepthFirstTraversalAsString());
    MatcherAssert.assertThat(list, Matchers.contains("ROOT",
        "A", "A1-1", "A1-1-1", "A1-2", "A1-2-1", "A1-3", "A1-3-1",
        "B", "B1-1", "B1-1-1", "B1-2", "B1-2-1", "B1-3", "B1-3-1"));
  }

  /**
   * 广度优先遍历
   */
  @Test
  public void breadthFirstTraversalTest() {
    Node<String> tree = genTree();
    List<String> list = new LinkedList<>();
    tree.breadthFirstTraversal(node -> list.add(node.data));
    assertThat(tree.getChildren().size(), Matchers.is(2));
    log.info("{}", tree.writeBreadthFirstTraversalAsString());
    MatcherAssert.assertThat(list, Matchers.contains("ROOT",
        "A", "B",
        "A1-1", "A1-2", "A1-3", "B1-1", "B1-2", "B1-3",
        "A1-1-1", "A1-2-1", "A1-3-1", "B1-1-1", "B1-2-1", "B1-3-1"));
  }

  /**
   * 遍历查找
   */
  @Test
  public void findTest() {
    Node<String> tree = genTree();

    Node<String> node = tree.find(n -> n.data.equals("B"));
    MatcherAssert.assertThat(node.data, Matchers.is("B"));
    assertThat(node.getChildren().size(), Matchers.is(3));

    node = tree.find(n -> n.data.equals("B1-1"));
    MatcherAssert.assertThat(node.data, Matchers.is("B1-1"));
    assertThat(node.getChildren().size(), Matchers.is(1));

    node = tree.find(n -> n.data.equals("A1-3-1"));
    MatcherAssert.assertThat(node.data, Matchers.is("A1-3-1"));

    node = tree.find(n -> n.data.equals("Oops"));
    MatcherAssert.assertThat(node, Matchers.is(Matchers.nullValue()));
  }

  /**
   * 遍历删除
   */
  @Test
  public void removeTest() {
    Node<String> tree = genTree();
    tree.remove(n -> n.data.equals("B1-1"));
    log.info("{}", tree.writeDepthFirstTraversalAsString());

    List<String> list = new LinkedList<>();
    tree.depthFirstTraversal(node -> list.add(node.data));
    MatcherAssert.assertThat(list, Matchers.contains("ROOT",
        "A", "A1-1", "A1-1-1", "A1-2", "A1-2-1", "A1-3", "A1-3-1",
        "B", "B1-2", "B1-2-1", "B1-3", "B1-3-1"));
  }

  private Node<String> genTree() {
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
}
