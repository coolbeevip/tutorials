package com.coolbeevip.crdts.wurmlouch;

import com.netopyr.wurmloch.crdt.GSet;
import com.netopyr.wurmloch.store.LocalCrdtStore;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class GrowOnlySetTest {

  @Test
  public void test() {
    // 创建两个节点，并连接成集群
    LocalCrdtStore store1 = new LocalCrdtStore();
    LocalCrdtStore store2 = new LocalCrdtStore();
    store1.connect(store2);

    GSet<Integer> replica1 = store1.createGSet("ID_1");
    GSet<Integer> replica2 = store2.<Integer>findGSet("ID_1").get();

    replica1.add(1);
    replica2.add(2);

    assertThat(replica1, Matchers.containsInAnyOrder(1, 2));
    assertThat(replica2, Matchers.containsInAnyOrder(1, 2));

    // 断开
    store1.disconnect(store2);

    replica1.add(3);
    replica2.add(4);

    assertThat(replica1, Matchers.containsInAnyOrder(1, 2, 3));
    assertThat(replica2, Matchers.containsInAnyOrder(1, 2, 4));

    // 重新连接连接
    store1.connect(store2);
    assertThat(replica1, Matchers.containsInAnyOrder(1, 2, 3, 4));
    assertThat(replica2, Matchers.containsInAnyOrder(1, 2, 3, 4));
  }
}
