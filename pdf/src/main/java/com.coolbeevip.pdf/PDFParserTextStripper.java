package com.coolbeevip.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class PDFParserTextStripper extends PDFTextStripper {

  private List<Pattern> ignoreListPattern = Arrays.asList(
      Pattern.compile("^Copyright.*"),
      Pattern.compile("^Updated.*"),
      Pattern.compile("^开发者布道⼿册"),
      Pattern.compile("^图 .*"),
      Pattern.compile("^\\d+$"),
      Pattern.compile("^\\d+\\.\\d+$"));
  private Map<String, Integer> lineMap = new HashMap<>();

  private StringBuffer buffer = new StringBuffer();

  public PDFParserTextStripper(PDDocument pdd) throws IOException {
    super();
    document = pdd;
  }

  public void stripPage(int pageNr) throws IOException {
    this.setStartPage(pageNr + 1);
    this.setEndPage(pageNr + 1);
    Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
    writeText(document,
        dummy); // This call starts the parsing process and calls writeString repeatedly.
  }

  @Override
  protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
//    if (lineMap.containsKey(string)){
//      lineMap.put(string,lineMap.get(string)+1);
//    }else {
//      lineMap.put(string,1);
//    }
    if (ignoreListPattern.stream().anyMatch(pattern -> pattern.matcher(string).matches())) {
      return;
    }
    if (textPositions.get(0).getFontSize() == 24.0) {
      System.out.println("\n## " + string);
      buffer.setLength(0);
    } else {
      for (TextPosition text : textPositions) {
        if (text.getUnicode().equals("。")) {
          buffer.append(text.getUnicode());
          System.out.println(buffer.toString());
          buffer.setLength(0);
        } else {
          buffer.append(text.getUnicode());
        }
      }
    }
    //System.out.println("[" + textPositions.get(0).getFontSize() + "]" + string);
  }
}