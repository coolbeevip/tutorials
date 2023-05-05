package com.coolbeevip.msoffice.word.validation;

import com.beust.jcommander.Parameter;

public class CliArgs {

  @Parameter(names = "-docx", description = "Word 文件路径", required = true)
  public String docx;
}
