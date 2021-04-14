package com.coolbeevip.akka.quickstart.protocols;

/**
 * 加入房间
 * @author zhanglei
 */
public interface JoinRequest {

  class Join implements JoinRequest {

    public Visitor visitor;

    public Join(Visitor visitor) {
      this.visitor = visitor;
    }
  }
}
