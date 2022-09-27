package com.coolbeevip.xml.cmdb;

import java.util.Iterator;

public class ResourceNodeIterator<T> implements Iterator<ResourceNode<T>> {
  enum ProcessStages {
    ProcessParent, ProcessChildCurNode, ProcessChildSubNode
  }

  private ProcessStages doNext;

  private ResourceNode<T> next;

  private Iterator<ResourceNode<T>> childrenCurNodeIter;

  private Iterator<ResourceNode<T>> childrenSubNodeIter;

  private ResourceNode<T> ResourceNode;

  public ResourceNodeIterator(ResourceNode<T> ResourceNode) {
    this.ResourceNode = ResourceNode;
    this.doNext = ProcessStages.ProcessParent;
    this.childrenCurNodeIter = ResourceNode.children.iterator();
  }

  @Override
  public boolean hasNext() {

    if (this.doNext == ProcessStages.ProcessParent) {
      this.next = this.ResourceNode;
      this.doNext = ProcessStages.ProcessChildCurNode;
      return true;
    }

    if (this.doNext == ProcessStages.ProcessChildCurNode) {
      if (childrenCurNodeIter.hasNext()) {
        ResourceNode<T> childDirect = childrenCurNodeIter.next();
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
  public ResourceNode<T> next() {
    return this.next;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
