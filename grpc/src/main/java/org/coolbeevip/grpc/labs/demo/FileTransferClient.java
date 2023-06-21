package org.coolbeevip.grpc.labs.demo;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.coolbeevip.grpc.labs.demo.grpc.FileContent;
import org.coolbeevip.grpc.labs.demo.grpc.FileMeta;
import org.coolbeevip.grpc.labs.demo.grpc.FileTransferServiceGrpc;
import org.coolbeevip.grpc.labs.demo.grpc.UploadFileRequest;
import org.coolbeevip.grpc.labs.demo.grpc.UploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FileTransferClient {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final int DEFAULT_BATCH_SIZE = 4096;
  private final ManagedChannel channel;
  private final FileTransferServiceGrpc.FileTransferServiceBlockingStub blockingStub;
  private final FileTransferServiceGrpc.FileTransferServiceStub asyncStub;

  public FileTransferClient(String host, int port) {
    channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext().build();
    blockingStub = FileTransferServiceGrpc.newBlockingStub(channel);
    asyncStub = FileTransferServiceGrpc.newStub(channel);
  }

  //客户端流数据
  public FileUploadResponse uploadFile(File file, long timeout, TimeUnit unit) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    FileUploadResponse fileUploadResponse = new FileUploadResponse();
    final UploadFileResponse[] response = new UploadFileResponse[1];
    long beginTime = System.currentTimeMillis();
    StreamObserver<UploadFileRequest> requestStreamObserver = asyncStub.uploadFile(new StreamObserver<UploadFileResponse>() {
      @Override
      public void onNext(UploadFileResponse uploadFileResponse) {
        LOG.info("File upload process status {}, serverTimeMillis {} ms", uploadFileResponse.getStatus(), uploadFileResponse.getServerTimeMillis());
        response[0] = uploadFileResponse;
      }

      @Override
      public void onError(Throwable throwable) {
        LOG.error("File upload error", throwable);
        fileUploadResponse.setSuccess(false);
        fileUploadResponse.setServerTimeMillis(response[0].getServerTimeMillis());
        latch.countDown();
      }

      @Override
      public void onCompleted() {
        LOG.info("File upload completed");
        fileUploadResponse.setSuccess(true);
        fileUploadResponse.setServerTimeMillis(response[0].getServerTimeMillis());
        latch.countDown();
      }
    });

    try {
      // upload file metadata
      UploadFileRequest metadata = UploadFileRequest.newBuilder()
          .setFileMeta(FileMeta.newBuilder()
              .setName(file.getName())
              .setSize(file.length())
              .setLastModified(file.lastModified())
              .build())
          .build();
      requestStreamObserver.onNext(metadata);

      // upload file content
      try (InputStream inputStream = Files.newInputStream(file.toPath())) {
        byte[] bytes = new byte[DEFAULT_BATCH_SIZE];
        int size;
        while ((size = inputStream.read(bytes)) > 0) {
          UploadFileRequest uploadRequest = UploadFileRequest.newBuilder()
              .setFileContent(FileContent.newBuilder().setContent(ByteString.copyFrom(bytes, 0, size)).build())
              .build();
          requestStreamObserver.onNext(uploadRequest);
        }
      }
    } finally {
      requestStreamObserver.onCompleted();
      latch.await(timeout, unit);
      fileUploadResponse.setClientTimeMillis(System.currentTimeMillis() - beginTime);
      fileUploadResponse.setFileId(response[0].getFileId());
      fileUploadResponse.setFileName(response[0].getFileName());
      fileUploadResponse.setFileSize(response[0].getFileSize());
      return fileUploadResponse;
    }
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  class FileUploadResponse {
    private String fileId;
    private String fileName;
    private long fileSize;
    private boolean success;
    private long clientTimeMillis;
    private long serverTimeMillis;

    public String getFileId() {
      return fileId;
    }

    public void setFileId(String fileId) {
      this.fileId = fileId;
    }

    public String getFileName() {
      return fileName;
    }

    public void setFileName(String fileName) {
      this.fileName = fileName;
    }

    public long getFileSize() {
      return fileSize;
    }

    public void setFileSize(long fileSize) {
      this.fileSize = fileSize;
    }

    public boolean isSuccess() {
      return success;
    }

    public void setSuccess(boolean success) {
      this.success = success;
    }

    public long getClientTimeMillis() {
      return clientTimeMillis;
    }

    public void setClientTimeMillis(long clientTimeMillis) {
      this.clientTimeMillis = clientTimeMillis;
    }

    public long getServerTimeMillis() {
      return serverTimeMillis;
    }

    public void setServerTimeMillis(long serverTimeMillis) {
      this.serverTimeMillis = serverTimeMillis;
    }

    @Override
    public String toString() {
      return "FileUploadResponse{" +
          "fileId='" + fileId + '\'' +
          ", fileName='" + fileName + '\'' +
          ", fileSize=" + fileSize +
          ", success=" + success +
          ", clientTimeMillis=" + clientTimeMillis +
          ", serverTimeMillis=" + serverTimeMillis +
          '}';
    }
  }
}