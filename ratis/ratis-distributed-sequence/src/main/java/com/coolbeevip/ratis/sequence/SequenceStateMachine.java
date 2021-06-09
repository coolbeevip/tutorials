package com.coolbeevip.ratis.sequence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ratis.proto.RaftProtos;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.protocol.TermIndex;
import org.apache.ratis.server.raftlog.RaftLog;
import org.apache.ratis.server.storage.RaftStorage;
import org.apache.ratis.statemachine.TransactionContext;
import org.apache.ratis.statemachine.impl.BaseStateMachine;
import org.apache.ratis.statemachine.impl.SimpleStateMachineStorage;
import org.apache.ratis.statemachine.impl.SingleFileSnapshotInfo;
import org.apache.ratis.util.JavaUtils;

/**
 * @author zhanglei
 */
public class SequenceStateMachine extends BaseStateMachine {

  private final SimpleStateMachineStorage storage =
      new SimpleStateMachineStorage();
  private AtomicInteger counter = new AtomicInteger(0);

  @Override
  public void initialize(RaftServer server, RaftGroupId groupId,
      RaftStorage raftStorage) throws IOException {
    super.initialize(server, groupId, raftStorage);
    this.storage.init(raftStorage);
    load(storage.getLatestSnapshot());
  }

  @Override
  public void reinitialize() throws IOException {
    load(storage.getLatestSnapshot());
  }

  @Override
  public long takeSnapshot() {
    //get the last applied index
    final TermIndex last = getLastAppliedTermIndex();

    //create a file with a proper name to store the snapshot
    final File snapshotFile =
        storage.getSnapshotFile(last.getTerm(), last.getIndex());

    //serialize the counter object and write it into the snapshot file
    try (ObjectOutputStream out = new ObjectOutputStream(
        new BufferedOutputStream(new FileOutputStream(snapshotFile)))) {
      out.writeObject(counter);
    } catch (IOException ioe) {
      LOG.warn("Failed to write snapshot file \"" + snapshotFile
          + "\", last applied index=" + last);
    }

    //return the index of the stored snapshot (which is the last applied one)
    return last.getIndex();
  }

  private long load(SingleFileSnapshotInfo snapshot) throws IOException {
    //check the snapshot nullity
    if (snapshot == null) {
      LOG.warn("The snapshot info is null.");
      return RaftLog.INVALID_LOG_INDEX;
    }

    //check the existance of the snapshot file
    final File snapshotFile = snapshot.getFile().getPath().toFile();
    if (!snapshotFile.exists()) {
      LOG.warn("The snapshot file {} does not exist for snapshot {}",
          snapshotFile, snapshot);
      return RaftLog.INVALID_LOG_INDEX;
    }

    //load the TermIndex object for the snapshot using the file name pattern of
    // the snapshot
    final TermIndex last =
        SimpleStateMachineStorage.getTermIndexFromSnapshotFile(snapshotFile);

    //read the file and cast it to the AtomicInteger and set the counter
    try (ObjectInputStream in = new ObjectInputStream(
        new BufferedInputStream(new FileInputStream(snapshotFile)))) {
      //set the last applied termIndex to the termIndex of the snapshot
      setLastAppliedTermIndex(last);

      //read, cast and set the counter
      counter = JavaUtils.cast(in.readObject());
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }

    return last.getIndex();
  }

  @Override
  public CompletableFuture<Message> query(Message request) {
    String msg = request.getContent().toString(Charset.defaultCharset());
    if (!msg.equals("GET")) {
      return CompletableFuture.completedFuture(
          Message.valueOf("Invalid Command"));
    }
    return CompletableFuture.completedFuture(
        Message.valueOf(counter.toString()));
  }

  // 事件
  @Override
  public CompletableFuture<Message> applyTransaction(TransactionContext trx) {
    final RaftProtos.LogEntryProto entry = trx.getLogEntry();

    //check if the command is valid
    String logData = entry.getStateMachineLogEntry().getLogData()
        .toString(Charset.defaultCharset());
    if (!logData.equals("INCREMENT")) {
      return CompletableFuture.completedFuture(
          Message.valueOf("Invalid Command"));
    }
    //update the last applied term and index
    final long index = entry.getIndex();
    updateLastAppliedTermIndex(entry.getTerm(), index);

    //actual execution of the command: increment the counter
    counter.incrementAndGet();

    //return the new value of the counter to the client
    final CompletableFuture<Message> f =
        CompletableFuture.completedFuture(Message.valueOf(counter.toString()));

    //if leader, log the incremented value and it's log index
    if (trx.getServerRole() == RaftProtos.RaftPeerRole.LEADER) {
      LOG.info("{}: Increment to {}", index, counter.toString());
    }

    return f;
  }
}
