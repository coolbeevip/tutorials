package org.coolbeevip.netty.labs.single;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 单线程模型
 *
 * @author zhanglei
 *
 * */
public class EchoServer {

  private final int port;

  public EchoServer(int port) {
    this.port = port;
  }

  public void start() throws Exception {
    final EchoServerHandler serverHandler = new EchoServerHandler();
    // NioEventLoopGroup 线程池大小为 1
    // 构造 ServerBootstrap group 的时候只传入一个 parentGroup 和 childGroup 公用一个
    EventLoopGroup group = new NioEventLoopGroup(1);
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(group)
          .channel(NioServerSocketChannel.class)
          .localAddress(new InetSocketAddress(port))
          .childHandler(new ChannelInitializer<SocketChannel>() {
            // 新的连接接入时，一个新的子 Channel 会被添加到 Pipeline 中
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(serverHandler);
            }
          });
      ChannelFuture f = bootstrap.bind().sync();
      f.channel().closeFuture().sync();
    } finally {
      group.shutdownGracefully();
    }
  }
}