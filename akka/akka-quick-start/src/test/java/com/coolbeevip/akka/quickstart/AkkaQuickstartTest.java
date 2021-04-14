package com.coolbeevip.akka.quickstart;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.ClassRule;
import org.junit.Test;

public class AkkaQuickstartTest {

  @ClassRule
  public static final TestKitJunitResource testKit = new TestKitJunitResource();

  @Test
  public void testGreeterActorSendingOfGreeting() {
    TestProbe<VisitorActor.Greeted> testProbe = testKit.createTestProbe();
    ActorRef<VisitorActor.Greet> underTest = testKit.spawn(VisitorActor.create(), "greeter");
    underTest.tell(new VisitorActor.Greet("Charles", testProbe.getRef()));
    testProbe.expectMessage(new VisitorActor.Greeted("Charles", underTest));
  }
}
