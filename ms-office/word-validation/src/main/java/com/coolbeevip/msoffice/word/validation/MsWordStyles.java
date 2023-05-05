package com.coolbeevip.msoffice.word.validation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class MsWordStyles {

  public static String TABLE_FIVE_DESC = "_表格五号字体_xxxxxx_cn";
  public static String TOC_TITLE_DESC = "_目录前和中标题_xxxxxx_cn";
  public static String MAIN_BODY_DESC = "_全文正文_xxxxxx_cn";
  public static String TITLE_1_DESC = "heading 1";
  public static String TITLE_2_DESC = "heading 2";
  public static String TITLE_3_DESC = "heading 3";
  public static String TITLE_4_DESC = "heading 4";
  public static String CODE_DESC = "_代码_xxxxxx_cn";
  public static double PAGE_LEFT = 3.17;
  public static double PAGE_RIGHT = 3.17;

  public static double PAGE_TOP = 2.54;
  public static double PAGE_BOTTOM = 2.54;
  public static Map<String, String> STYLE_IDS = new HashMap() {{
    put(TITLE_1_DESC, "2");
    put(TITLE_2_DESC, "3");
    put(TITLE_3_DESC, "4");
    put(TITLE_4_DESC, "5");
    put(CODE_DESC, "26");
    put(TOC_TITLE_DESC, "30");
    put(MAIN_BODY_DESC, "43");
    put(TABLE_FIVE_DESC, "57");
  }};

  public static Set<String> TOC_NAMES = new HashSet() {{
    add("图目录");
    add("表目录");
  }};

  public static Set<String> DEPRECATED = new HashSet() {{
    add("_表格_表头标题行_xxxxxx_cn");
    add("_表格_正文格式_xxxxxx_cn");
    add("_表格_表头标题行_xxxxxx_cn 字符");
    add("_表格_正文格式_加粗_xxxxxx_cn");
  }};

  public static Pattern TOC_TABLE_TITLE_PATTERN = Pattern.compile("表\\d+-\\d+", Pattern.CASE_INSENSITIVE);
  public static Pattern TOC_PICTURE_TITLE_PATTERN = Pattern.compile("图\\d+-\\d+", Pattern.CASE_INSENSITIVE);
  public static Pattern TOC_TITLE_PATTERN = Pattern.compile("^\\d+(|.\\d+|.\\d+.\\d|.\\d+.\\d.\\d|.\\d+.\\d.\\d.\\d|) .*\\d+$", Pattern.CASE_INSENSITIVE);
  public static List<String> FUZZY_WORDS = Arrays.asList("差不多", "大约", "显然");
}
