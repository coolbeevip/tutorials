package com.coolbeevip.msoffice.ppt.validation;

import com.beust.jcommander.Parameter;

public class CliArgs {

  @Parameter(names = "-pptx", description = "PPT 文件路径", required = true)
  public String docx;
}
