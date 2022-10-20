package com.coolbeevip.structures.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Tree Data Structure
 */
public class Node<T> {

  public T data;
  public Node<T> parent;
  public List<Node<T>> children;
  private List<Node<T>> elementsIndex;

  public Node(T data) {
    this.data = data;
    this.children = new LinkedList<>();
    this.elementsIndex = new LinkedList<>();
    this.elementsIndex.add(this);
  }

  public boolean isRoot() {
    return parent == null;
  }

  public boolean isLeaf() {
    return children.size() == 0;
  }


  public Node<T> addChild(T child) {
    Node<T> node = new Node<T>(child);
    node.parent = this;
    this.children.add(node);
    this.registerChildForSearch(node);
    return node;
  }

  public List<Node<T>> getChildren() {
    return this.children;
  }

  public Node<T> getParent() {
    return this.parent;
  }

  public void removeChild(Node childToRemove) {
    if (this.children.isEmpty()) {
      return;
    } else if (this.children.contains(childToRemove)) {
      this.children.remove(childToRemove);
      return;
    } else {
      for (Node child : this.children) {
        child.removeChild(childToRemove);
      }
    }
  }

  /**
   * 获取当前节点的层
   *
   * @return
   */
  public int getLevel() {
    if (this.isRoot()) {
      return 0;
    } else {
      return parent.getLevel() + 1;
    }
  }

  /**
   * 递归为当前节点以及当前节点的所有父节点增加新的节点
   *
   * @param node
   */
  private void registerChildForSearch(Node<T> node) {
    elementsIndex.add(node);
    if (parent != null) {
      parent.registerChildForSearch(node);
    }
  }

  /**
   * 查找
   */
  public Node<T> find(Predicate<T> predicate) {
    for (Node<T> element : this.elementsIndex) {
      if (predicate.test(element)) {
        return element;
      }
    }
    return null;
  }

  /**
   * 删除
   */
  public void remove(Predicate<T> predicate) {
    this.breadthFirstTraversal(node -> {
      if (predicate.test(node)) {
        node.getParent().removeChild(node);
      }
    });
  }

  /**
   * 深度优先遍历
   */
  public void depthFirstTraversal(Consumer<T> consumer) {
    Node<T> current = this;
    consumer.accept(current);
    Iterator<Node<T>> it = current.children.iterator();
    while (it.hasNext()) {
      Node<T> child = it.next();
      child.depthFirstTraversal(consumer);
    }
  }

  /**
   * 广度优先遍历
   */
  public void breadthFirstTraversal(Consumer<T> consumer) {
    Node<T> current = this;
    Queue<Node<T>> queue = new LinkedList<>();
    queue.add(current);
    while (!queue.isEmpty()) {
      current = queue.poll();
      consumer.accept(current);
      queue.addAll(current.children);
    }
  }

  public String writeDepthFirstTraversalAsString() {
    StringBuilder builder = new StringBuilder();
    this.depthFirstTraversal(n -> {
      builder.append(createIndent(n.getLevel()) + n.data + "\n");
    });
    return builder.toString();
  }

  public String writeBreadthFirstTraversalAsString() {
    StringBuilder builder = new StringBuilder();
    this.breadthFirstTraversal(n -> {
      builder.append(createIndent(n.getLevel()) + n.data + "\n");
    });
    return builder.toString();
  }

  public String writeValueAsString(NodeFormatter formatter){
    return formatter.format(this);
  }

  private String createIndent(int depth) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      sb.append(' ');
    }
    return sb.toString();
  }
}
