package com.coolbeevip.msoffice.word.validation;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.util.ArrayList;
import java.util.List;

public class XWPFParagraphJson {
  private List<XWPFRunJson> runJsonList = new ArrayList<>();

  public XWPFParagraphJson(XWPFParagraph paragraph) {
    paragraph.getRuns().forEach(run -> {
      this.runJsonList.add(new XWPFRunJson(run));
    });
  }

  public List<XWPFRunJson> getRunJsonList() {
    return runJsonList;
  }
}
