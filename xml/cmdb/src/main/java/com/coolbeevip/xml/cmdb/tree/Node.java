package com.coolbeevip.xml.cmdb.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Tree Data Structure
 */
public class Node<T> implements Iterable<Node<T>> {

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

  public void remove() {
    this.parent.removeChild(this);
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
   * 从当前节点及其所有子节点中搜索某节点
   *
   * @param cmp
   * @return
   */
  public Node<T> findTreeNode(Comparable<T> cmp) {
    for (Node<T> element : this.elementsIndex) {
      T elData = element.data;
      if (cmp.compareTo(elData) == 0) return element;
    }
    return null;
  }

  @Override
  public java.util.Iterator<Node<T>> iterator() {
    return new NodeIterator<T>(this);
  }

  /**
   * 深度优先遍历
   * */
  public void depthFirstTraversal(Handler<T> handler) {
    Node<T> current = this;
    handler.doHandler(current);
    Iterator<Node<T>> it = current.children.iterator();
    while (it.hasNext()){
      Node<T> child = it.next();
      child.depthFirstTraversal(handler);
    }
//    for (Node<T> child : current.children) {
//      child.depthFirstTraversal(handler);
//    }
  }

  /**
   * 广度优先遍历
   * */
  public void breadthFirstTraversal(Handler<T> handler) {
    Node<T> current = this;
    Queue<Node<T>> queue = new LinkedList<>();
    queue.add(current);
    while (!queue.isEmpty()) {
      current = queue.poll();
      handler.doHandler(current);
      queue.addAll(current.children);
    }
  }

  private String createIndent(int depth) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      sb.append(' ');
    }
    return sb.toString();
  }
}
