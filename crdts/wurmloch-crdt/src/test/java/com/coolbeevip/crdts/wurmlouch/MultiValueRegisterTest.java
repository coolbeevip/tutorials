package com.coolbeevip.crdts.wurmlouch;

import com.netopyr.wurmloch.crdt.MVRegister;
import com.netopyr.wurmloch.store.LocalCrdtStore;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * An MV-Register (Multi-Value Register) is another implementation of a register.
 * It avoids the kind of data loss, that is inherent to any kind of last-writer-wins strategy.
 * Instead if the value of a MV-Register is changed concurrently, it keeps all values.
 * Therefore the result of the get()-method is a collection.
 */
public class MultiValueRegisterTest {
  @Test
  public void test() throws InterruptedException {
    // 创建两个节点，并连接成集群
    LocalCrdtStore store1 = new LocalCrdtStore();
    LocalCrdtStore store2 = new LocalCrdtStore();
    store1.connect(store2);

    MVRegister<String> replica1 = store1.createMVRegister("ID_1");
    MVRegister<String> replica2 = store2.<String>findMVRegister("ID_1").get();

    replica1.set("apple");
    replica2.set("banana");

    assertThat(replica1.get(), Matchers.contains("banana"));
    assertThat(replica2.get(), Matchers.contains("banana"));

    // 断开
    store1.disconnect(store2);
    replica1.set("strawberry");
    replica2.set("pear");
    assertThat(replica1.get(), Matchers.contains("strawberry"));
    assertThat(replica2.get(), Matchers.contains("pear"));

    // 重新连接连接
    store1.connect(store2);
    assertThat(replica1.get(), Matchers.containsInAnyOrder("strawberry", "pear"));
    assertThat(replica2.get(), Matchers.containsInAnyOrder("strawberry", "pear"));

    replica2.set("orange");
    assertThat(replica1.get(), Matchers.contains("orange"));
    assertThat(replica2.get(), Matchers.contains("orange"));
  }
}

