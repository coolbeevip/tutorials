package com.coolbeevip.msoffice.word.validation;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public class XWPFRunJson {
  private final String text;
  private final String color;
  private final String fontFamily;
  private final String fontName;
  private final Double fontSize;
  private final int textPosition;
  public XWPFRunJson(XWPFRun run) {
    this.text = run.getText(0);
    this.color = run.getColor();
    this.fontFamily = run.getFontFamily();
    this.fontName = run.getFontName();
    this.fontSize = run.getFontSizeAsDouble();
    this.textPosition = run.getTextPosition();
  }

  public String getText() {
    return text;
  }

  public String getColor() {
    return color;
  }

  public String getFontFamily() {
    return fontFamily;
  }

  public String getFontName() {
    return fontName;
  }

  public Double getFontSize() {
    return fontSize;
  }

  public int getTextPosition() {
    return textPosition;
  }
}
