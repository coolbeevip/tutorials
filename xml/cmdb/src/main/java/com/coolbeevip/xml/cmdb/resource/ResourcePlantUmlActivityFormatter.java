package com.coolbeevip.xml.cmdb.resource;

import com.coolbeevip.xml.cmdb.Resource;
import com.coolbeevip.xml.cmdb.tree.OperateType;
import com.coolbeevip.xml.cmdb.tree.format.AbstractPlantUmlActivityFormatter;

public class ResourcePlantUmlActivityFormatter<T extends Resource> extends AbstractPlantUmlActivityFormatter<T> {

  public ResourcePlantUmlActivityFormatter(OperateType operateType) {
    super(operateType);
  }

  @Override
  protected String getName(T value) {
    return value.getId();
  }
}
