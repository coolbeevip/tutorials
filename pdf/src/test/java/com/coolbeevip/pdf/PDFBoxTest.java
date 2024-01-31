package com.coolbeevip.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.junit.jupiter.api.Test;

public class PDFBoxTest {

  @Test
  public void givenSamplePdf_whenUsingApachePdfBox_thenCompareOutput() throws IOException {

    String expectedText = "Hello World!\n";
    File file = new File(
        "/Users/zhanglei/Library/CloudStorage/OneDrive-个人/文档/developer-advocacy-handbook-jimmysong-zh-v20220324.pdf");
    try (PDDocument pdd = Loader.loadPDF(file)) {
      PDFParserTextStripper stripper = new PDFParserTextStripper(pdd);
      stripper.setSortByPosition(true);
      for (int i = 0; i < pdd.getNumberOfPages(); i++) {
        stripper.stripPage(i);
      }
    }
  }
}