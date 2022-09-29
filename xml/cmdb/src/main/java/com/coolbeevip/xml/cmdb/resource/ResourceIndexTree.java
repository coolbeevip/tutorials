package com.coolbeevip.xml.cmdb.resource;

public class ResourceIndexTree {
  private final String id;
  private final String parentId;

  public ResourceIndexTree(String id, String parentId) {
    this.id = id;
    this.parentId = parentId;
  }

  public String getId() {
    return id;
  }

  public String getParentId() {
    return parentId;
  }
}
