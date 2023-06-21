package org.coolbeevip.grpc.labs.demo;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.coolbeevip.grpc.labs.demo.grpc.FileMeta;
import org.coolbeevip.grpc.labs.demo.grpc.FileTransferServiceGrpc;
import org.coolbeevip.grpc.labs.demo.grpc.ResponseStatus;
import org.coolbeevip.grpc.labs.demo.grpc.UploadFileRequest;
import org.coolbeevip.grpc.labs.demo.grpc.UploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

/**
 * @author zhanglei
 */
public class FileTransferService extends FileTransferServiceGrpc.FileTransferServiceImplBase {
  private static final Path BASE_PATH = Paths.get("target");
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public StreamObserver<UploadFileRequest> uploadFile(StreamObserver<UploadFileResponse> responseObserver) {
    return new StreamObserver<UploadFileRequest>() {
      FileMeta fileMeta;
      OutputStream writer;
      ResponseStatus status = ResponseStatus.IN_PROGRESS;

      long processBeginTime;

      @Override
      public void onNext(UploadFileRequest uploadFileRequest) {
        try {
          if (uploadFileRequest.hasFileMeta()) {
            fileMeta = uploadFileRequest.getFileMeta();
            processBeginTime = System.currentTimeMillis();
            writer = openFile(uploadFileRequest);
          } else {
            writeFile(writer, uploadFileRequest.getFileContent().getContent());
          }
        } catch (IOException e) {
          this.onError(e);
        }
      }

      @Override
      public void onError(Throwable throwable) {
        status = ResponseStatus.FAILED;
        this.onCompleted();
      }

      @Override
      public void onCompleted() {
        closeFile(writer);
        status = ResponseStatus.IN_PROGRESS.equals(status) ? ResponseStatus.SUCCESS : status;
        UploadFileResponse response = UploadFileResponse.newBuilder()
            .setStatus(status)
            .setFileId(UUID.randomUUID().toString())
            .setFileName(fileMeta.getName())
            .setFileSize(fileMeta.getSize())
            .setServerTimeMillis(System.currentTimeMillis() - processBeginTime)
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
      }
    };
  }

  private OutputStream openFile(UploadFileRequest request) throws IOException {
    String fileName = request.getFileMeta().getName();
    LOG.info("Receive file: {}/{}", BASE_PATH, request.getFileMeta().getName());
    return Files.newOutputStream(BASE_PATH.resolve(fileName), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
  }

  private void writeFile(OutputStream writer, ByteString content) throws IOException {
    LOG.info("Write file: {}", content.size());
    writer.write(content.toByteArray());
    writer.flush();
  }

  private void closeFile(OutputStream writer) {
    try {
      writer.close();
      LOG.info("Close file");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}