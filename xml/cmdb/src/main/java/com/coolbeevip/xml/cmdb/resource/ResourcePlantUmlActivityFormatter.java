package com.coolbeevip.xml.cmdb.resource;

import com.coolbeevip.structures.tree.OperateType;
import com.coolbeevip.structures.tree.format.AbstractPlantUmlActivityFormatter;
import com.coolbeevip.xml.cmdb.Resource;

public class ResourcePlantUmlActivityFormatter<T extends Resource> extends AbstractPlantUmlActivityFormatter<T> {

  public ResourcePlantUmlActivityFormatter(OperateType operateType) {
    super(operateType);
  }

  @Override
  protected String getName(T value) {
    return value.getId();
  }
}
