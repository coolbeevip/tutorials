package com.coolbeevip.ratis.sequence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcConfigKeys;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.RaftServerConfigKeys;
import org.apache.ratis.util.LifeCycle.State;
import org.apache.ratis.util.NetUtils;
import org.apache.ratis.util.TimeDuration;

/**
 * @author zhanglei
 */
@Slf4j
@Builder
public class SequenceServer {

  private static final UUID CLUSTER_GROUP_ID = UUID
      .fromString("02511d47-d67c-49a3-9011-abb3109a44c1");
  private final String address;
  private final Path storagePath = Paths.get("ratis-data");
  private final List<String> peerAddress;
  private RaftProperties properties;
  private RaftServer server;

  public CompletableFuture<Void> start() throws IOException {
    initRaftProperties();
    initRaftStorageDir();
    return initRaftCurrentPeer();
  }

  private void initRaftProperties() {
    this.properties = new RaftProperties();
    RaftServerConfigKeys.Rpc
        .setTimeoutMin(properties, TimeDuration.valueOf(300, TimeUnit.MILLISECONDS));
    RaftServerConfigKeys.Rpc
        .setTimeoutMax(properties, TimeDuration.valueOf(600, TimeUnit.MILLISECONDS));

    // 启动 SNAPSHOT 并设置每 200 条事件触发，目前有BUG
    // Snapshot.setAutoTriggerEnabled(properties,true);
    // Snapshot.setAutoTriggerThreshold(properties,400000L);
  }

  private void initRaftStorageDir() throws IOException {
    if (Files.notExists(storagePath)) {
      Files.createDirectory(storagePath);
      log.info("Initialize the data directory {}", storagePath.toAbsolutePath());
    }
  }

  private CompletableFuture<Void> initRaftCurrentPeer() throws IOException {
    // 创建当前节点
    RaftPeer currentPeer = RaftPeer.newBuilder().setId(addressToId(this.address))
        .setAddress(this.address).build();

    // 设置存储目录
    File raftStorageDir = Paths.get(storagePath.toString(), currentPeer.getId().toString())
        .toFile();
    RaftServerConfigKeys.setStorageDir(properties, Collections.singletonList(raftStorageDir));

    // 设置监听端口
    final int port = NetUtils.createSocketAddr(currentPeer.getAddress()).getPort();
    GrpcConfigKeys.Server.setPort(properties, port);

    // 创建计数器状态机
    List<RaftPeer> raftPeers = peerAddress.stream()
        .map(addr -> RaftPeer.newBuilder().setId(addressToId(addr)).setAddress(addr).build())
        .collect(Collectors.toList());
    RaftGroup raftGroup = RaftGroup.valueOf(RaftGroupId.valueOf(CLUSTER_GROUP_ID), raftPeers);

    SequenceStateMachine sequenceStateMachine = new SequenceStateMachine();
    server = RaftServer.newBuilder()
        .setGroup(raftGroup)
        .setProperties(properties)
        .setServerId(currentPeer.getId())
        .setStateMachine(sequenceStateMachine)
        .build();
    server.start();

    return CompletableFuture.runAsync(() -> {
      while (server.getLifeCycleState() != State.RUNNING) {
        try {
          log.info("Server state {}",server.getLifeCycleState().name());
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
          log.error("{} was interrupted: {}", this, e);
          Thread.currentThread().interrupt();
        }
      }
    });
  }

  public void stop() {
    if (server.getLifeCycleState() == State.RUNNING) {
      try {
        server.close();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  private String addressToId(String address) {
    return address.replace(".", "_").replace(":", "_");
  }

}
