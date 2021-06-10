package com.coolbeevip.ratis.sequence;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.client.RaftClient;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcFactory;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftClientReply;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeer;

/**
 * @author zhanglei
 */
@Slf4j
@Builder
public class Sequence {

  private static final UUID CLUSTER_GROUP_ID = UUID
      .fromString("02511d47-d67c-49a3-9011-abb3109a44c1");
  private List<String> peerAddress;
  private RaftClient raftClient;

  public CompletableFuture<Void> start() {
    List<RaftPeer> raftPeers = this.peerAddress.stream()
        .map(addr -> RaftPeer.newBuilder().setId(addr).setAddress(addr).build())
        .collect(Collectors.toList());
    RaftGroup raftGroup = RaftGroup.valueOf(RaftGroupId.valueOf(CLUSTER_GROUP_ID), raftPeers);

    RaftProperties raftProperties = new RaftProperties();
    RaftClient.Builder builder = RaftClient.newBuilder()
        .setProperties(raftProperties)
        .setRaftGroup(raftGroup)
        .setClientRpc(
            new GrpcFactory(new Parameters())
                .newRaftClientRpc(ClientId.randomId(), raftProperties));
    raftClient = builder.build();

    return CompletableFuture.runAsync(() -> {
      String peerAddressInfo = this.peerAddress.stream().collect(Collectors.joining(","));
      while (raftClient.getLeaderId() == null) {
        try {
          log.info("Connect to {}...", peerAddressInfo);
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
          log.error("{} was interrupted: {}", this, e);
          log.error("Connect fails", e);
          Thread.currentThread().interrupt();
        }
      }
      log.info("Connect to {} successful, leader is {}", peerAddressInfo, raftClient.getLeaderId());
    });
  }

  public String genericId() throws IOException {
    if (raftClient != null && raftClient.getLeaderId() != null) {
      raftClient.io().send(Message.valueOf("INCREMENT"));
      RaftClientReply count = raftClient.io().sendReadOnly(Message.valueOf("GET"));
      String response = count.getMessage().getContent().toString(Charset.defaultCharset());
      return response;
    } else {
      throw new RuntimeException("集群不可用");
    }
  }

}
