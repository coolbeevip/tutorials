package com.coolbeevip.crdts.wurmlouch;

import com.netopyr.wurmloch.crdt.LWWRegister;
import com.netopyr.wurmloch.store.LocalCrdtStore;
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;

public class LastWriterWinsRegisterTest {
  @Test
  public void test() throws InterruptedException {
    // 创建两个节点，并连接成集群
    LocalCrdtStore store1 = new LocalCrdtStore();
    LocalCrdtStore store2 = new LocalCrdtStore();
    store1.connect(store2);

    LWWRegister<Integer> replica1 = store1.createLWWRegister("ID_1");
    LWWRegister<Integer> replica2 = store2.<Integer>findLWWRegister("ID_1").get();

    replica1.set(1);
    replica2.set(2);

    assertThat(replica1.get(), Matchers.is(2));

    // 断开
    store1.disconnect(store2);
    replica1.set(3);
    replica2.set(4);
    assertThat(replica1.get(), Matchers.is(3));
    assertThat(replica2.get(), Matchers.is(4));

    // 重新连接连接
    store1.connect(store2);
    Awaitility.await().atMost(10, SECONDS).until(() -> {
      if(replica1.get() == 4 && replica2.get() == 4){
        return true;
      }else{
        SECONDS.sleep(1);
        return false;
      }
    });
  }
}

