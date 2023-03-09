package com.coolbeevip.crdts.wurmlouch;

import com.netopyr.wurmloch.crdt.MVRegister;
import com.netopyr.wurmloch.store.LocalCrdtStore;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Note that an MV-Register is not a Set.
 * As can be seen in the following more complex example,
 * an MV-Register keeps track of which values were override and can be eliminated.
 */
public class MultiValueComplexRegisterTest {
  @Test
  public void test() throws InterruptedException {
    // 创建两个节点，并连接成集群
    LocalCrdtStore store1 = new LocalCrdtStore();
    LocalCrdtStore store2 = new LocalCrdtStore();
    LocalCrdtStore store3 = new LocalCrdtStore();
    store1.connect(store2);
    store1.connect(store3);

    MVRegister<String> replica1 = store1.createMVRegister("ID_1");
    MVRegister<String> replica2 = store2.<String>findMVRegister("ID_1").get();
    MVRegister<String> replica3 = store3.<String>findMVRegister("ID_1").get();

    replica1.set("apple");
    replica2.set("banana");
    replica3.set("orange");

    assertThat(replica1.get(), Matchers.contains("orange"));
    assertThat(replica2.get(), Matchers.contains("orange"));
    assertThat(replica3.get(), Matchers.contains("orange"));

    // 断开 store1 和 store3
    store1.disconnect(store3);
    replica1.set("strawberry");
    replica3.set("pear");
    assertThat(replica1.get(), Matchers.contains("strawberry"));
    assertThat(replica2.get(), Matchers.contains("strawberry"));
    assertThat(replica3.get(), Matchers.contains("pear"));

    // 断开 store1 和 store2，重连 store1 和 store3
    store1.disconnect(store2);
    store1.connect(store3);
    assertThat(replica1.get(), Matchers.containsInAnyOrder("strawberry", "pear"));
    assertThat(replica2.get(), Matchers.containsInAnyOrder("strawberry"));
    assertThat(replica3.get(), Matchers.containsInAnyOrder("strawberry", "pear"));

    // 重连 store1 和 store2
    store1.connect(store2);
    assertThat(replica1.get(), Matchers.containsInAnyOrder("strawberry", "pear"));
    assertThat(replica2.get(), Matchers.containsInAnyOrder("strawberry", "pear"));
    assertThat(replica3.get(), Matchers.containsInAnyOrder("strawberry", "pear"));

    replica3.set("mongo");
    assertThat(replica1.get(), Matchers.containsInAnyOrder("mongo"));
    assertThat(replica2.get(), Matchers.containsInAnyOrder("mongo"));
    assertThat(replica3.get(), Matchers.containsInAnyOrder("mongo"));
  }
}

