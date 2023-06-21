package org.coolbeevip.grpc.labs.demo;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FileTransferTest {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static GrpcServer server;
  private static int port = 58084;
  FileTransferClient client;

  @BeforeClass
  public static void before() {
    server = new GrpcServer(port);
    server.start();
  }

  @AfterClass
  public static void after() {
    server.stop();
  }

  @Test
  public void uploadFileTest() throws IOException, InterruptedException {
    File file = createFile(1024 * 1024 * 10);
    try {
      FileTransferClient.FileUploadResponse response = client.uploadFile(file, 10, SECONDS);
      System.out.println(response);
      Awaitility.await().atMost(10, SECONDS).until(() -> response.isSuccess());
    } finally {
      file.delete();
    }
  }

  @Before
  public void setup() {
    client = new FileTransferClient("127.0.0.1", port);
  }

  @After
  public void clean() throws InterruptedException {
    client.shutdown();
  }

  private File createFile(long size) throws IOException {
    Path path = Files.createTempFile("demo", ".txt");
    try (RandomAccessFile r = new RandomAccessFile(path.toFile(), "rw")) {
      r.setLength(size);
    }
    return path.toFile();
  }
}