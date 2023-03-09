package com.coolbeevip.crdts.wurmlouch;

import com.netopyr.wurmloch.crdt.GCounter;
import com.netopyr.wurmloch.store.LocalCrdtStore;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class GrowOnlyCounterTest {

  @Test
  public void test() {
    // 创建两个节点，并连接成集群
    LocalCrdtStore store1 = new LocalCrdtStore();
    LocalCrdtStore store2 = new LocalCrdtStore();
    store1.connect(store2);

    GCounter replica1 = store1.createGCounter("ID_1");
    GCounter replica2 = store2.findGCounter("ID_1").get();

    replica1.increment();
    replica2.increment();

    assertThat(replica1.get(), Matchers.is(2L));
    assertThat(replica2.get(), Matchers.is(2L));

    // 断开
    store1.disconnect(store2);

    replica1.increment();
    replica2.increment();

    assertThat(replica1.get(), Matchers.is(3L));
    assertThat(replica2.get(), Matchers.is(3L));

    // 重新连接连接
    store1.connect(store2);
    assertThat(replica1.get(), Matchers.is(4L));
    assertThat(replica2.get(), Matchers.is(4L));
  }
}
