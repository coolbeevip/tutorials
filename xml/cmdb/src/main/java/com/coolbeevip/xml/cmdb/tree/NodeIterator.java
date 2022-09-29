package com.coolbeevip.xml.cmdb.tree;

import java.util.Iterator;

public class NodeIterator<T> implements Iterator<Node<T>> {
  enum ProcessStages {
    ProcessParent, ProcessChildCurNode, ProcessChildSubNode
  }

  private ProcessStages doNext;

  private Node<T> next;

  private Iterator<Node<T>> childrenCurNodeIter;

  private Iterator<Node<T>> childrenSubNodeIter;

  private Node<T> node;

  public NodeIterator(Node<T> node) {
    this.node = node;
    this.doNext = ProcessStages.ProcessParent;
    this.childrenCurNodeIter = node.children.iterator();
  }

  @Override
  public boolean hasNext() {

    if (this.doNext == ProcessStages.ProcessParent) {
      this.next = this.node;
      this.doNext = ProcessStages.ProcessChildCurNode;
      return true;
    }

    if (this.doNext == ProcessStages.ProcessChildCurNode) {
      if (childrenCurNodeIter.hasNext()) {
        Node<T> childDirect = childrenCurNodeIter.next();
        childrenSubNodeIter = childDirect.iterator();
        this.doNext = ProcessStages.ProcessChildSubNode;
        return hasNext();
      } else {
        this.doNext = null;
        return false;
      }
    }

    if (this.doNext == ProcessStages.ProcessChildSubNode) {
      if (childrenSubNodeIter.hasNext()) {
        this.next = childrenSubNodeIter.next();
        return true;
      } else {
        this.next = null;
        this.doNext = ProcessStages.ProcessChildCurNode;
        return hasNext();
      }
    }

    return false;
  }

  @Override
  public Node<T> next() {
    return this.next;
  }

  @Override
  public void remove() {
    if (this.doNext == ProcessStages.ProcessChildCurNode) {
      //this.childrenCurNodeIter.remove();
    } else if (this.doNext == ProcessStages.ProcessChildSubNode) {
      this.childrenSubNodeIter.remove();
    }

    // throw new UnsupportedOperationException();
  }
}
