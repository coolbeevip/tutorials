package com.coolbeevip.xml.cmdb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ResourceNode<T> implements Iterable<ResourceNode<T>> {

  public T data;
  public ResourceNode<T> parent;
  public List<ResourceNode<T>> children;
  private List<ResourceNode<T>> elementsIndex;

  public ResourceNode(T data) {
    this.data = data;
    this.children = new LinkedList<>();
    this.elementsIndex = new LinkedList<>();
    this.elementsIndex.add(this);
  }

  /**
   * 判断是否为根：根没有父节点
   *
   * @return
   */
  public boolean isRoot() {
    return parent == null;
  }

  /**
   * 判断是否为叶子节点：子节点没有子节点
   *
   * @return
   */
  public boolean isLeaf() {
    return children.size() == 0;
  }

  /**
   * 添加一个子节点
   *
   * @param child
   * @return
   */
  public ResourceNode<T> addChild(T child) {
    ResourceNode<T> childNode = new ResourceNode<T>(child);

    childNode.parent = this;

    this.children.add(childNode);

    this.registerChildForSearch(childNode);

    return childNode;
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
  private void registerChildForSearch(ResourceNode<T> node) {
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
  public ResourceNode<T> findTreeNode(Comparable<T> cmp) {
    for (ResourceNode<T> element : this.elementsIndex) {
      T elData = element.data;
      if (cmp.compareTo(elData) == 0) return element;
    }

    return null;
  }

  /**
   * 获取当前节点的迭代器
   *
   * @return
   */
  @Override
  public Iterator<ResourceNode<T>> iterator() {
    ResourceNodeIterator<T> iterator = new ResourceNodeIterator<T>(this);
    return iterator;
  }

  @Override
  public String toString() {
    return data != null ? data.toString() : "[tree data null]";
  }
}
