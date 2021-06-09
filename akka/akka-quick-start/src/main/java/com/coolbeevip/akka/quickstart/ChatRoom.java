package com.coolbeevip.akka.quickstart;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.coolbeevip.akka.quickstart.protocols.RoomMessage;
import com.coolbeevip.akka.quickstart.protocols.RoomMessage.VisitorLogin;
import com.coolbeevip.akka.quickstart.protocols.RoomMessage.VisitorLogout;
import com.coolbeevip.akka.quickstart.protocols.RoomMessage.VisitorSay;
import com.coolbeevip.akka.quickstart.protocols.VisitorMessage;
import com.coolbeevip.akka.quickstart.protocols.VisitorMessage.Say;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 聊天室 Actor System
 * @author zhanglei
 */
public class ChatRoom extends AbstractBehavior<RoomMessage> {

  /**
   * 存储房间所有访客
   */
  private Map<UUID, ActorRef<VisitorMessage>> visitors = new HashMap();

  private ChatRoom(ActorContext<RoomMessage> context) {
    super(context);
  }

  public static Behavior<RoomMessage> create() {
    return Behaviors.setup(ChatRoom::new);
  }

  @Override
  public Receive<RoomMessage> createReceive() {
    return newReceiveBuilder()
        .onMessage(VisitorLogin.class, this::onLogin)
        .onMessage(VisitorLogout.class, this::onLogout)
        .onMessage(VisitorSay.class, this::onSay)
        .build();
  }

  private Behavior<RoomMessage> onLogin(VisitorLogin login) {
    getContext().getLog().info("{} join",
        login.visitor.getTitle());
    ActorRef<VisitorMessage> actorRef = getContext()
        .spawn(VisitorActor.create(), login.visitor.name);

    // 给访问者发送登录响应消息
    actorRef.tell(new VisitorMessage.LoginResponse(login.visitor, 0, getContext().getSelf()));

    // 增加访客缓存
    visitors.put(login.visitor.id, actorRef);

    // 通知所有访客有新访客上线
    visitors.entrySet().stream().filter(entry -> !entry.getKey().equals(login.visitor.id))
        .forEach(entry -> {
          entry.getValue().tell(new Say(login.visitor.getTitle() + " online"));
        });

    return Behaviors.same();
  }

  private Behavior<RoomMessage> onLogout(VisitorLogout logout) {
    getContext().getLog().info("{} leave",
        logout.visitor.getTitle());

    // 移除访客缓存
    visitors.remove(logout.visitor.id);

    // 通知房间里所有人，访客离开
    visitors.entrySet().stream().forEach(entry -> {
      entry.getValue().tell(new Say("note: " + logout.visitor.getTitle() + " offline"));
    });
    return this;
  }

  /**
   * 对房间里的所有人发消息
   */
  private Behavior<RoomMessage> onSay(VisitorSay say) {
    visitors.entrySet().stream()
        .filter(entry -> !entry.getKey().equals(say.visitor.id))
        .forEach(entry -> {
          entry.getValue().tell(new Say(say.visitor.name + ": " + say.msg));
        });
    return this;
  }

}
