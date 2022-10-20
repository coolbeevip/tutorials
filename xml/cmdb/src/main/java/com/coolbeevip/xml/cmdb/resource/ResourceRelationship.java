package com.coolbeevip.xml.cmdb.resource;

public class ResourceRelationship {
  private final String id;
  private final String parentId;
  private final int value;

  public ResourceRelationship(String id, String parentId, int value) {
    this.id = id;
    this.parentId = parentId;
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public String getParentId() {
    return parentId;
  }

  public int getValue() {
    return value;
  }
}
