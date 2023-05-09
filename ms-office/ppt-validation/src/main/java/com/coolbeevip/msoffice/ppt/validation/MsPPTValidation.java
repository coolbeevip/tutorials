package com.coolbeevip.msoffice.ppt.validation;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.xmlbeans.XmlException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MsPPTValidation {

  public void validation(Path msPPTPath) throws IOException, XmlException {
    XMLSlideShow slideShow = new XMLSlideShow(Files.newInputStream(msPPTPath));
    List<String> errors = new ArrayList<>();
    try {
      slideShow.getSlides().forEach(slide -> {
        slide.getTitle();
        debug(String.format(ConsoleColors.BLUE + "slide=[%s] title=[%s] theme=[%s] show=[%s]" + ConsoleColors.RESET,
            slide.getSlideNumber(),
            slide.getTitle(),
            slide.getTheme().getName(),
            slide.getXmlObject().getShow()));
        slide.getXmlObject().getCSld().getSpTree().getSpList().forEach(ctShape -> {
          if (ctShape.getTxBody() != null) {
            ctShape.getTxBody().getPList().forEach(ctTextParagraph -> {
              ctTextParagraph.getRList().forEach(ctTextRun -> {
                debug(ctTextRun.getT());
              });
            });
          }
        });
      });
    } catch (Exception e) {
      System.out.println(ConsoleColors.RED + "ERROR" + ConsoleColors.RESET + ": " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (slideShow != null) slideShow.close();
    }
  }

  private void debug(String message) {
    System.out.println(message);
  }

  public void check(boolean expression, String message, List<String> errors) {
    try {
      Assert.isTrue(expression, message);
    } catch (IllegalArgumentException e) {
      errors.add(e.getMessage());
    }
  }
}
