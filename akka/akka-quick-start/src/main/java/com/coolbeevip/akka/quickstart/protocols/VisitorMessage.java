package com.coolbeevip.akka.quickstart.protocols;

import akka.actor.typed.ActorRef;
import com.coolbeevip.akka.quickstart.protocols.entity.Visitor;
import java.util.Objects;

/**
 * 访客消息协议
 * @author zhanglei
 */
public interface VisitorMessage {

  /**
   * 登录响应
   */
  class LoginResponse implements VisitorMessage {

    public final ActorRef<RoomMessage> room;
    public final Visitor visitor;
    public final int result;

    public LoginResponse(Visitor visitor, int result, ActorRef<RoomMessage> room) {
      this.visitor = visitor;
      this.result = result;
      this.room = room;
    }
  }

  class Say implements VisitorMessage {

    public final String msg;

    public Say(String msg) {
      this.msg = msg;
    }
  }

  /**
   * 提问
   */
  class Ask implements VisitorMessage {

    public final Visitor visitor;
    public final ActorRef<Answer> replyTo;
    public final String msg;

    public Ask(Visitor visitor,
        ActorRef<Answer> replyTo, String msg) {
      this.visitor = visitor;
      this.replyTo = replyTo;
      this.msg = msg;
    }

    @Override
    public int hashCode() {
      return Objects.hash(visitor, replyTo, msg);
    }
  }

  /**
   * 回答
   */
  class Answer implements VisitorMessage {

    public final Visitor visitor;
    public final ActorRef<Ask> from;
    public final String msg;

    public Answer(Visitor visitor,
        ActorRef<Ask> from,
        String msg) {
      this.visitor = visitor;
      this.from = from;
      this.msg = msg;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Answer answer = (Answer) o;
      return Objects.equals(visitor, answer.visitor) &&
          Objects.equals(from, answer.from) &&
          Objects.equals(msg, answer.msg);
    }

    @Override
    public int hashCode() {
      return Objects.hash(visitor, from, msg);
    }

    @Override
    public String toString() {
      return "Answer{" +
          "visitor=" + visitor +
          ", from=" + from +
          ", msg=" + msg +
          '}';
    }
  }
}
