package com.coolbeevip.akka.quickstart;

import akka.actor.typed.ActorSystem;
import java.io.IOException;

/**
 * @author zhanglei
 */
public class AkkaQuickstart {

  public static void main(String[] args) {
    //创建 ActorSystem
    final ActorSystem<ChatRoom.SayHello> chatroom = ActorSystem
      .create(ChatRoom.create(), "chatroom");

    chatroom.tell(new ChatRoom.SayHello("Charles"));

    try {
      System.out.println(">>> Press ENTER to exit <<<");
      System.in.read();
    } catch (IOException ignored) {
    } finally {
      chatroom.terminate();
    }
  }
}
