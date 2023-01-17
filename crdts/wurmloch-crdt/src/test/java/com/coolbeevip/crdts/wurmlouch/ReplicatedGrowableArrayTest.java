package com.coolbeevip.crdts.wurmlouch;

import com.netopyr.wurmloch.crdt.RGA;
import com.netopyr.wurmloch.store.LocalCrdtStore;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * An RGA (Replicated Growable Array) is a CRDT that behaves similarly to a List.
 * The elements have an order, and one can add and remove elements at specific positions.
 * In this implementation, it is not possible though to set values because it is not defined how concurrent sets should behave.
 * An UnsupportedOperationException will be thrown if the set() method is called.
 */
public class ReplicatedGrowableArrayTest {
  @Test
  public void test() {

    final LocalCrdtStore store1 = new LocalCrdtStore();
    final LocalCrdtStore store2 = new LocalCrdtStore();
    store1.connect(store2);

    final RGA<String> replica1 = store1.createRGA("ID_1");
    final RGA<String> replica2 = store2.<String>findRGA("ID_1").get();

    // add one entry to each replica
    replica1.add("apple");
    replica2.add("banana");

    // the stores are connected, thus the RGA is automatically synchronized
    assertThat(replica1, Matchers.contains("apple", "banana"));
    assertThat(replica2, Matchers.contains("apple", "banana"));

    // disconnect the stores simulating a network issue, offline mode etc.
    store1.disconnect(store2);

    // add one entry to each replica
    replica1.remove("banana");
    replica2.add(1, "strawberry");

    // the stores are not connected, thus the changes have only local effects
    assertThat(replica1, Matchers.containsInAnyOrder("apple"));
    assertThat(replica2, Matchers.containsInAnyOrder("apple", "strawberry", "banana"));

    // reconnect the stores
    store1.connect(store2);

    // the RGA is synchronized automatically
    assertThat(replica1, Matchers.containsInAnyOrder("apple", "strawberry"));
    assertThat(replica2, Matchers.containsInAnyOrder("apple", "strawberry"));

    // disconnect the stores
    store1.disconnect(store2);

    // set() is not supported in an RGA
    // if we try to simulate with a remove and add, we can see the problem
    replica1.remove(0);
    replica1.add("pear");
    replica2.remove(0);
    replica2.add("orange");

    // the first entry has been replaced
    assertThat(replica1, Matchers.containsInAnyOrder("pear", "strawberry"));
    assertThat(replica2, Matchers.containsInAnyOrder("orange", "strawberry"));

    // reconnect the stores
    store1.connect(store2);

    // we have actually added two elements, the RGA keeps both
    assertThat(replica1, Matchers.containsInAnyOrder("orange", "pear", "strawberry"));
    assertThat(replica2, Matchers.containsInAnyOrder("orange", "pear", "strawberry"));
  }
}

