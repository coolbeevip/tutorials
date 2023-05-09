package com.coolbeevip.msoffice.ppt.validation;

import com.beust.jcommander.JCommander;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.nio.file.Paths;

public class Cli {
  public static void main(String[] args) throws XmlException, IOException {
    CliArgs cliArgs = new CliArgs();
    JCommander.newBuilder()
        .addObject(cliArgs)
        .build()
        .parse(args);

    MsPPTValidation validation = new MsPPTValidation();
    validation.validation(Paths.get(cliArgs.docx));
  }
}
