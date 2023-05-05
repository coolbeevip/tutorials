package com.coolbeevip.msoffice.word.validation;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MsWordValidation {


  public void validation(Path msWordPath) throws IOException, XmlException {
    XWPFDocument document = new XWPFDocument(Files.newInputStream(msWordPath));
    List<String> errors = new ArrayList<>();
    try {
      // page margins
      check(convertPoundToCentimeter((BigInteger) document.getDocument().getBody().getSectPr().getPgMar().getLeft()) == MsWordStyles.PAGE_LEFT, "左页边距必须为 3.17cm", errors);
      check(convertPoundToCentimeter((BigInteger) document.getDocument().getBody().getSectPr().getPgMar().getRight()) == MsWordStyles.PAGE_RIGHT, "右页边距必须为 3.17cm", errors);
      check(convertPoundToCentimeter((BigInteger) document.getDocument().getBody().getSectPr().getPgMar().getTop()) == MsWordStyles.PAGE_TOP, "上页边距必须为 2.54cm", errors);
      check(convertPoundToCentimeter((BigInteger) document.getDocument().getBody().getSectPr().getPgMar().getBottom()) == MsWordStyles.PAGE_BOTTOM, "下页边距必须为 2.54cm", errors);

      // style
      document.getStyle().getStyleList().forEach(xwpfStyle -> {
        if (xwpfStyle.getName().getVal().equals(MsWordStyles.TABLE_FIVE_DESC)) {
          MsWordStyles.STYLE_IDS.put(MsWordStyles.TABLE_FIVE_DESC, xwpfStyle.getStyleId());
        } else if (xwpfStyle.getName().getVal().equals(MsWordStyles.TOC_TITLE_DESC)) {
          MsWordStyles.STYLE_IDS.put(MsWordStyles.TOC_TITLE_DESC, xwpfStyle.getStyleId());
        } else if (xwpfStyle.getName().getVal().equals(MsWordStyles.TITLE_1_DESC)) {
          MsWordStyles.STYLE_IDS.put(MsWordStyles.TITLE_1_DESC, xwpfStyle.getStyleId());
        } else if (xwpfStyle.getName().getVal().equals(MsWordStyles.TITLE_2_DESC)) {
          MsWordStyles.STYLE_IDS.put(MsWordStyles.TITLE_2_DESC, xwpfStyle.getStyleId());
        } else if (xwpfStyle.getName().getVal().equals(MsWordStyles.TITLE_3_DESC)) {
          MsWordStyles.STYLE_IDS.put(MsWordStyles.TITLE_3_DESC, xwpfStyle.getStyleId());
        } else if (xwpfStyle.getName().getVal().equals(MsWordStyles.TITLE_4_DESC)) {
          MsWordStyles.STYLE_IDS.put(MsWordStyles.TITLE_4_DESC, xwpfStyle.getStyleId());
        } else if (xwpfStyle.getName().getVal().equals(MsWordStyles.MAIN_BODY_DESC)) {
          MsWordStyles.STYLE_IDS.put(MsWordStyles.MAIN_BODY_DESC, xwpfStyle.getStyleId());
        } else if (xwpfStyle.getName().getVal().equals(MsWordStyles.CODE_DESC)) {
          MsWordStyles.STYLE_IDS.put(MsWordStyles.CODE_DESC, xwpfStyle.getStyleId());
        }
        // System.out.println(String.format("> style [%s] id [%s]", xwpfStyle.getName().getVal(), xwpfStyle.getStyleId()));
        check(!MsWordStyles.DEPRECATED.contains(xwpfStyle.getName().getVal()), "样式 [" + xwpfStyle.getName().getVal() + "] 已经废弃", errors);
      });

      // paragraphs check
      Map<Integer, String> tableNameIndexes = new HashMap<>();
      AtomicInteger tocTableCount = new AtomicInteger();
      AtomicInteger tocPictureCount = new AtomicInteger();
      AtomicInteger tocTitleCount = new AtomicInteger();
      AtomicInteger realTitleCount = new AtomicInteger();
      Map<String, String> titleIndexAndNames = new HashMap<>();
      List<XWPFParagraph> paragraphs = document.getParagraphs();
      int tocLevel = -1;
      for (XWPFParagraph paragraph : paragraphs) {
        if (paragraph.getText().trim().length() > 0) {
          // System.out.println(String.format("> paragraph [%s] style [%s]", paragraph.getText(), paragraph.getCTP().getPPr().getPStyle() != null ? paragraph.getCTP().getPPr().getPStyle().getVal() : "null"));
          if (MsWordStyles.TOC_NAMES.contains(paragraph.getText())) {
            // toc style check
            check(MsWordStyles.STYLE_IDS.get(MsWordStyles.TOC_TITLE_DESC).equals(paragraph.getCTP().getPPr().getPStyle().getVal()), "目录样式必须为 " + MsWordStyles.TOC_TITLE_DESC, errors);
          } else if (MsWordStyles.TOC_TABLE_TITLE_PATTERN.matcher(paragraph.getText()).find()) {
            //System.out.println(String.format("table title [%s]", paragraph.getText()));
            tableNameIndexes.put(tocTableCount.get(), paragraph.getText());
            tocTableCount.incrementAndGet();
          } else if (MsWordStyles.TOC_PICTURE_TITLE_PATTERN.matcher(paragraph.getText()).find()) {
            // System.out.println(String.format("picture title [%s]", paragraph.getText()));
            tocPictureCount.incrementAndGet();
          } else if (MsWordStyles.TOC_TITLE_PATTERN.matcher(paragraph.getText()).find()) {
            // title structure check
            String[] titleParts = paragraph.getText().split(" ");
            String num = titleParts[0];
            if (paragraph.getText().indexOf("附录") == -1) {
              String name = titleParts[1].split("\t")[0];
              titleIndexAndNames.put(num, name);
              String page = titleParts[1].split("\t")[1];
            } else {
              String name = titleParts[1] + titleParts[2].split("\t")[0];
              titleIndexAndNames.put(num, name);
              String page = titleParts[2].split("\t")[1];
            }

            check(num.split(".").length < 5, "标题 [" + paragraph.getText() + "] 不允许超过四级", errors);

            if (tocLevel == -1) {
              check(num.equals("1"), "标题 [" + paragraph.getText() + "] 应该以 1 开头", errors);
            } else {
              check(num.split(".").length - tocLevel < 2, "标题 [" + paragraph.getText() + "] 跨级使用", errors);
            }
            tocLevel = num.split(".").length;
            tocTitleCount.incrementAndGet();
            // System.out.println(String.format("toc title [%s]", paragraph.getText()));
          } else if (paragraph.getCTP().getPPr().getPStyle() != null && (
              MsWordStyles.STYLE_IDS.get(MsWordStyles.TITLE_1_DESC).equals(paragraph.getCTP().getPPr().getPStyle().getVal()) ||
                  MsWordStyles.STYLE_IDS.get(MsWordStyles.TITLE_2_DESC).equals(paragraph.getCTP().getPPr().getPStyle().getVal()) ||
                  MsWordStyles.STYLE_IDS.get(MsWordStyles.TITLE_3_DESC).equals(paragraph.getCTP().getPPr().getPStyle().getVal()))) {
            // System.out.println(String.format("real title [%s]", paragraph.getText()));
            realTitleCount.incrementAndGet();
          } else {
            // check paragraph length
            AtomicInteger paragraphWordLength = new AtomicInteger();
            paragraph.getRuns().stream().filter(run -> run.getText(0) != null).forEach(run -> {
              try {
                if (run.getCTR().getRPr() != null && run.getCTR().getRPr().getRFontsList().size() > 0) {
                  CTFonts ctFonts = run.getCTR().getRPr().getRFontsList().get(0);
                  if (ctFonts.getHint() != null) {
                    if (ctFonts.getHint().toString().equals("eastAsia")) {
                      try {
                        // 34 gbk word in line to 68 bytes
                        paragraphWordLength.addAndGet(run.getText(0).getBytes("GBK").length);
                      } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                      }
                    } else {
                      // 62 us ascii word in line to 62 bytes
                      paragraphWordLength.addAndGet(run.getText(0).getBytes(StandardCharsets.US_ASCII).length);
                    }
                  } else if (ctFonts.getAscii() != null && ctFonts.getAscii().equals("Cambria")) {
                    List<String> words = new LinkedList<>();
                    ((XWPFParagraph) run.getParent()).getRuns().forEach(run1 -> {
                      if (run1.getCTR().getRPr() != null) {
                        CTFonts ctFonts1 = run1.getCTR().getRPr().getRFontsList().get(0);
                        if ("Cambria".equals(ctFonts1.getAscii())) {
                          words.add("<<非法字符>>");
                        }
                      } else {
                        words.add(run1.getText(0));
                      }
                    });
                    check(false, "段落中存在非法字符 [" + words.stream().collect(Collectors.joining()) + "]", errors);
                  }
                }
              } catch (Exception e) {
                e.printStackTrace();
              }
            });
            // 62 * 7 = 434
            check(paragraphWordLength.get() <= 434, "疑似超过段落最大行数 7 行 [" + paragraph.getText() + "]", errors);
            // System.out.println(paragraph.getText());
            // System.out.println(paragraph.getCTP().getPPr().getPStyle() != null ? paragraph.getCTP().getPPr().getPStyle().getVal() : null);
            if (paragraph.getCTP().getPPr().getPStyle() != null &&
                MsWordStyles.STYLE_IDS.get(MsWordStyles.MAIN_BODY_DESC).equals(paragraph.getCTP().getPPr().getPStyle().getVal())) {
              if (paragraph.getCTP().getPPr().getNumPr() == null) {
                check(paragraph.getText().endsWith("。") || paragraph.getText().endsWith("；") || paragraph.getText().endsWith("："), "段落或者句子必须以标点符号。；：结尾 [" + paragraph.getText() + "]", errors);
              } else {
                check(paragraph.getText().endsWith("。") || paragraph.getText().endsWith("；"), "列表项必须以标点符号。；结尾 [" + paragraph.getText() + "]", errors);
              }
            }
          }
          XWPFParagraphJson xwpfParagraphJson = new XWPFParagraphJson(paragraph);
          xwpfParagraphJson.getRunJsonList().forEach(runJson -> {
            // fuzzy word check
            if (runJson.getText() != null) {
              MsWordStyles.FUZZY_WORDS.stream().forEach(word -> {
                check(!runJson.getText().contains(word), "找到模糊表达用词: " + word, errors);
              });
            }
          });
        }
      }

      check(tocTableCount.get() == document.getTables().size(), "表目录数量 " + tocTableCount.get() + " 与表实际数量 " + document.getTables().size() + " 不一致", errors);
      check(tocPictureCount.get() == document.getAllPictures().size(), "图目录数量 " + tocPictureCount.get() + " 与图实际数量 " + document.getAllPictures().size() + " 不一致", errors);
      check(tocTitleCount.get() == realTitleCount.get(), "目录数量 " + tocPictureCount.get() + " 与实际数量 " + realTitleCount.get() + " 不一致", errors);

      // table cell style
      document.getTables().forEach(xwpfTable -> {
        // check table style
        AtomicInteger row = new AtomicInteger();
        xwpfTable.getCTTbl().getTrList().forEach(ctRow -> {
          ctRow.getTcList().forEach(cttc -> {
            cttc.getPList().forEach(ctp -> {
              ctp.getRList().stream().flatMap(r -> Arrays.stream(r.getTArray())).forEach(t -> {
                // System.out.println(String.format("table row[%d] cell [%s] style: [%s]", row.get(), t.getStringValue(), ctp.getPPr().getPStyle() != null ? ctp.getPPr().getPStyle().getVal() : "null"));
                check(ctp.getPPr().getPStyle() != null && ctp.getPPr().getPStyle().getVal().equals(MsWordStyles.STYLE_IDS.get(MsWordStyles.TABLE_FIVE_DESC)), "table cell [" + t.getStringValue() + "] style must be " + MsWordStyles.TABLE_FIVE_DESC, errors);
                long styleBoldPartCount = ctp.getRList().stream().filter(ctr -> ctr.getRPr() != null)
                    .map(ctr -> ctr.getRPr())
                    .flatMap(ctrPr -> ctrPr.getBCsList().stream())
                    .filter(bcs -> bcs.getVal() == null || bcs.getVal().equals(true)).count();

                long focusFontSizeCount = ctp.getRList().stream().filter(ctr -> ctr.getRPr() != null)
                    .map(ctr -> ctr.getRPr())
                    .flatMap(ctrPr -> ctrPr.getSzList().stream())
                    .filter(sz -> !sz.getVal().equals(-1)).count();

                if (row.get() == 0) {
                  check(styleBoldPartCount == ctp.getRList().size(), "表格标题 [" + t.getStringValue() + "] 字体必须为粗体", errors);
                } else {
                  check(styleBoldPartCount == 0, "表格值 [" + t.getStringValue() + "] 字体不能为粗体", errors);
                }
                check(focusFontSizeCount == 0, "表格值 [" + t.getStringValue() + "] 字体被强制设置，请使用样式中的字体", errors);
              });

            });
          });
          row.getAndIncrement();
        });
      });

      // table cell empty check
      AtomicInteger tableIndex = new AtomicInteger();
      document.getTables().forEach(xwpfTable -> {
        xwpfTable.getRows().forEach(xwpfTableRow -> {
          xwpfTableRow.getTableCells().forEach(xwpfTableCell -> {
            check(xwpfTableCell.getText().trim().length() > 0, "表格 [" + tableNameIndexes.get(tableIndex.get()) + "] 中存在空白单元格", errors);
            check(!xwpfTableCell.getText().equals("同上"), "表格 [" + tableNameIndexes.get(tableIndex.get()) + "] 中不能使用【同上】文字", errors);
          });
        });
        tableIndex.getAndIncrement();
      });


      if (errors.isEmpty()) {
        System.out.println(ConsoleColors.GREEN + "OK" + ConsoleColors.RESET + ": 检查成功");
      } else {
        errors.stream().forEach(e -> System.out.println(ConsoleColors.RED + "ERROR" + ConsoleColors.RESET + ": " + e));
      }
    } catch (Exception e) {
      System.out.println(ConsoleColors.RED + "ERROR" + ConsoleColors.RESET + ": " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (document != null) document.close();
    }
  }

  public void check(boolean expression, String message, List<String> errors) {
    try {
      Assert.isTrue(expression, message);
    } catch (IllegalArgumentException e) {
      errors.add(e.getMessage());
    }
  }

  private double convertPoundToCentimeter(BigInteger value) {
    double cmvalue = value.doubleValue() / 20 / 28.35;
    BigDecimal b = new BigDecimal(cmvalue);
    return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
  }
}
