package com.coolbeevip.akka.quickstart;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import com.coolbeevip.akka.quickstart.protocols.RoomMessage;
import com.coolbeevip.akka.quickstart.protocols.RoomMessage.VisitorLogin;
import com.coolbeevip.akka.quickstart.protocols.VisitorMessage;
import com.coolbeevip.akka.quickstart.protocols.entity.Visitor;
import com.coolbeevip.akka.quickstart.protocols.entity.VisitorGender;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.UUID;

public class AkkaQuickstartTest {


  static final ActorTestKit testKit = ActorTestKit.create();

  //@ClassRule
  //public static final TestKitJunitResource testKit = new TestKitJunitResource();

  @AfterClass
  public static void cleanup() {
    testKit.shutdownTestKit();
  }

  @Test
  public void testLoginChatRoom() {

    ActorRef<RoomMessage> actorRefRoom = testKit.spawn(ChatRoom.create(), "chatroom");

    TestProbe<VisitorMessage> visitorProbe = testKit.createTestProbe();

    Visitor thomas = new Visitor(UUID.randomUUID(), "Thomas", VisitorGender.male);
    actorRefRoom.tell(new VisitorLogin(thomas));

//    adminProbe.expectMessageClass(AdminMessage.VisitorJoin.class);
//    visitorProbe.expectNoMessage();
    //visitorProbe.expectMessageClass(VisitorMessage.LoginResponse.class);
    //visitorProbe.expectMessage(new VisitorMessage.LoginResponse(thomas.id, 0));

    //visitorProbe.expectMessage(new VisitorMessage.Say("Welcome " + thomas.getTitle() + " -- Admin"));
  }
}
