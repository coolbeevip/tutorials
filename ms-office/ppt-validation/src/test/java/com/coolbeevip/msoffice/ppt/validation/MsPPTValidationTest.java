package com.coolbeevip.msoffice.ppt.validation;


import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class MsPPTValidationTest {

  @Test
  public void test() throws IOException, XmlException {
    String path = "/Users/zhanglei/Desktop/misc/p0-word/ppt.pptx";
    MsPPTValidation validation = new MsPPTValidation();
    validation.validation(Paths.get(path));
  }
}
