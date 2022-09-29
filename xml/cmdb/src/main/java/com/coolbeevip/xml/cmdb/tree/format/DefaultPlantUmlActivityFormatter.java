package com.coolbeevip.xml.cmdb.tree.format;

import com.coolbeevip.xml.cmdb.tree.OperateType;

public class DefaultPlantUmlActivityFormatter<T> extends AbstractPlantUmlActivityFormatter<T> {

  public DefaultPlantUmlActivityFormatter(OperateType operateType) {
    super(operateType);
  }

  @Override
  protected String getName(T value) {
    return value.toString();
  }
}
