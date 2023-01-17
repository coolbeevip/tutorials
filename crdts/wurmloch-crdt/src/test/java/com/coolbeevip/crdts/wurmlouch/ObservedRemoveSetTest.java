package com.coolbeevip.crdts.wurmlouch;

import com.netopyr.wurmloch.crdt.ORSet;
import com.netopyr.wurmloch.store.LocalCrdtStore;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * The OR-Set (Observed-Remove Set) is a CRDT which probably comes closes to the expected behavior of a Set.
 * The basic idea is, that only elements which add-operation is visible to a replica can be removed from that replica.
 * That means for example, if an element is added in one replica and at the same time removed from a second,
 * non-synchronized replica, it is still contained in the OR-Set, because the add-operation was not visible to the second replica yet.
 */
public class ObservedRemoveSetTest {
  @Test
  public void test() {

    final LocalCrdtStore store1 = new LocalCrdtStore();
    final LocalCrdtStore store2 = new LocalCrdtStore();
    store1.connect(store2);

    final ORSet<String> replica1 = store1.createORSet("ID_1");
    final ORSet<String> replica2 = store2.<String>findORSet("ID_1").get();

    // add one entry to each replica
    replica1.add("apple");
    replica2.add("banana");

    // the stores are connected, thus the G-Set is automatically synchronized
    assertThat(replica1, Matchers.containsInAnyOrder("apple", "banana"));
    assertThat(replica2, Matchers.containsInAnyOrder("apple", "banana"));

    // disconnect the stores simulating a network issue, offline mode etc.
    store1.disconnect(store2);

    // remove one of the entries
    replica1.remove("banana");
    replica2.add("strawberry");

    // the stores are not connected, thus the changes have only local effects
    assertThat(replica1, Matchers.containsInAnyOrder("apple"));
    assertThat(replica2, Matchers.containsInAnyOrder("apple", "banana", "strawberry"));

    // reconnect the stores
    store1.connect(store2);

    // "banana" was added before both stores got disconnected, therefore it is now removed during synchronization
    assertThat(replica1, Matchers.containsInAnyOrder("apple", "strawberry"));
    assertThat(replica2, Matchers.containsInAnyOrder("apple", "strawberry"));

    // disconnect the stores again
    store1.disconnect(store2);

    // add one entry to each replica
    replica1.add("pear");
    replica2.add("pear");
    replica2.remove("pear");

    // "pear" was added in both stores concurrently, but immediately removed from replica2
    assertThat(replica1, Matchers.containsInAnyOrder("apple", "strawberry", "pear"));
    assertThat(replica2, Matchers.containsInAnyOrder("apple", "strawberry"));

    // reconnect the stores
    store1.connect(store2);

    // "pear" was added in both replicas concurrently
    // this means that the add-operation of "pear" to replica1 was not visible to replica2
    // therefore removing "pear" from replica2 does not include removing "pear" from replica1
    // as a result "pear" reappears in the merged Sets
    assertThat(replica1, Matchers.containsInAnyOrder("apple", "strawberry", "pear"));
    assertThat(replica2, Matchers.containsInAnyOrder("apple", "strawberry", "pear"));
  }
}

