package com.coolbeevip.msoffice.word.validation;


import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class MsWordValidationTest {

  @Test
  public void test() throws IOException, XmlException {
    String path = "/Users/zhanglei/Desktop/misc/word-validation/word.docx";
    MsWordValidation validation = new MsWordValidation();
    validation.validation(Paths.get(path));
  }
}
