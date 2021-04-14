package com.coolbeevip.akka.quickstart;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * 聊天室 Actor System
 * @author zhanglei
 */
public class ChatRoom extends AbstractBehavior<ChatRoom.SayHello> {

  public static class SayHello {

    public final String name;

    public SayHello(String name) {
      this.name = name;
    }
  }

  private final ActorRef<VisitorActor.Greet> visitorActor;

  public static Behavior<SayHello> create() {
    return Behaviors.setup(ChatRoom::new);
  }

  private ChatRoom(ActorContext<SayHello> context) {
    super(context);
    // 创建一个 Actor
    visitorActor = context.spawn(VisitorActor.create(), "greeter");
  }

  @Override
  public Receive<SayHello> createReceive() {
    return newReceiveBuilder().onMessage(SayHello.class, this::onSayHello).build();
  }

  private Behavior<SayHello> onSayHello(SayHello command) {
    //#create-actors
    ActorRef<VisitorActor.Greeted> replyTo =
      getContext().spawn(OwnerActor.create(3), command.name);
    visitorActor.tell(new VisitorActor.Greet(command.name, replyTo));
    //#create-actors
    return this;
  }
}
