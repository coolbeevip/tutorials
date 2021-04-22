package com.coolbeevip.akka.quickstart.protocols;

import com.coolbeevip.akka.quickstart.protocols.entity.Visitor;

/**
 * 聊天室消息协议
 * @author zhanglei
 */
public interface RoomMessage {

  /**
   * 登录
   */
  class VisitorLogin implements RoomMessage {

    public final Visitor visitor;

    public VisitorLogin(Visitor visitor) {
      this.visitor = visitor;
    }
  }

  /**
   * 登出
   */
  class VisitorLogout implements RoomMessage {

    public final Visitor visitor;

    public VisitorLogout(Visitor visitor) {
      this.visitor = visitor;
    }
  }

  /**
   * 发言
   */
  class VisitorSay implements RoomMessage {

    public final Visitor visitor;
    public final String msg;

    public VisitorSay(Visitor visitor, String msg) {
      this.visitor = visitor;
      this.msg = msg;
    }
  }
}
