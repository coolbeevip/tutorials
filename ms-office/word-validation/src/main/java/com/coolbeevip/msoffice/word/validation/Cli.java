package com.coolbeevip.msoffice.word.validation;

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

    MsWordValidation validation = new MsWordValidation();
    validation.validation(Paths.get(cliArgs.docx));
  }
}
