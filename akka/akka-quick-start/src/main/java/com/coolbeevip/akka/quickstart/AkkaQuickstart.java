package com.coolbeevip.akka.quickstart;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import com.coolbeevip.akka.quickstart.protocols.RoomMessage;
import com.coolbeevip.akka.quickstart.protocols.RoomMessage.VisitorLogin;
import com.coolbeevip.akka.quickstart.protocols.RoomMessage.VisitorLogout;
import com.coolbeevip.akka.quickstart.protocols.VisitorMessage;
import com.coolbeevip.akka.quickstart.protocols.entity.Visitor;
import com.coolbeevip.akka.quickstart.protocols.entity.VisitorGender;
import java.io.IOException;
import java.util.UUID;

/**
 * @author zhanglei
 */
public class AkkaQuickstart {

  public static void main(String[] args) {
    //创建 ActorSystem
    final ActorSystem<RoomMessage> chatroom = ActorSystem
      .create(ChatRoom.create(), "chatroom");


    Visitor thomas = new Visitor(UUID.randomUUID(), "Thomas", VisitorGender.male);
    Visitor suzy = new Visitor(UUID.randomUUID(), "Suzy", VisitorGender.female);

    // 两人加入聊天室
    chatroom.tell(new VisitorLogin(thomas));
    chatroom.tell(new VisitorLogin(suzy));

    try {
      System.out.println(">>> Press ENTER to exit <<<");
      System.in.read();
    } catch (IOException ignored) {
    } finally {

      // 两人离开聊天室
      chatroom.tell(new VisitorLogout(thomas));
      chatroom.tell(new VisitorLogout(suzy));

      chatroom.terminate();
    }
  }
}
