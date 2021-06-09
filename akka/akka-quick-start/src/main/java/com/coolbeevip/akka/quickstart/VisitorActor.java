package com.coolbeevip.akka.quickstart;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.coolbeevip.akka.quickstart.protocols.RoomMessage.VisitorSay;
import com.coolbeevip.akka.quickstart.protocols.VisitorMessage;

/**
 * 访客
 * @author zhanglei
 */
public class VisitorActor extends AbstractBehavior<VisitorMessage> {

  private VisitorActor(ActorContext<VisitorMessage> context) {
    super(context);
  }

  public static Behavior<VisitorMessage> create() {
    return Behaviors.setup(VisitorActor::new);
  }

  @Override
  public Receive<VisitorMessage> createReceive() {
    return newReceiveBuilder()
        .onMessage(VisitorMessage.Ask.class, this::onAsk)
        .onMessage(VisitorMessage.Answer.class, this::onAnswer)
        .onMessage(VisitorMessage.Say.class, this::onSay)
        .onMessage(VisitorMessage.LoginResponse.class, this::onLoginResponse)
        .build();
  }

  private Behavior<VisitorMessage> onAsk(VisitorMessage.Ask ask) {
    getContext().getLog().info("{} ask {}", ask.visitor.name, ask.msg);
    // ask.replyTo.tell(new ConversationMessage.Answer(null, getContext().getSelf()));
    return this;
  }

  private Behavior<VisitorMessage> onAnswer(VisitorMessage.Answer answer) {
    getContext().getLog().info("{} answer {}", answer.visitor.name, answer.msg);
    return this;
  }

  private Behavior<VisitorMessage> onSay(VisitorMessage.Say say) {
    getContext().getLog().info("Receive: {}", say.msg);
    return this;
  }

  private Behavior<VisitorMessage> onLoginResponse(VisitorMessage.LoginResponse loginResponse) {
    if (loginResponse.result == 0) {
      // 登录成功后跟房间所有人打招呼
      loginResponse.room
          .tell(new VisitorSay(loginResponse.visitor, "Hi, I'm " + loginResponse.visitor.name));
    } else {
      getContext().getLog().error("Receive Login Fail result {}", loginResponse.result);
    }
    return this;
  }
}

