package org.coolbeevip.netty.labs.reactorext;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;

/**
 * 主从多线程模型
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
    // bossgroup 线程池大小为 1，workergroup 线程池大小依赖cpu自动计算
    // 构造 ServerBootstrap 传入 bossgroup 和 workergroup 分别作为 parentGroup 和 childGroup
    EventLoopGroup bossgroup = new NioEventLoopGroup(4);
    EventLoopGroup workergroup = new NioEventLoopGroup();
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(bossgroup,workergroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 100)
          .childOption(ChannelOption.SO_KEEPALIVE, true)
          .handler(new LoggingHandler(LogLevel.INFO))
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
      bossgroup.shutdownGracefully();
      workergroup.shutdownGracefully();
    }
  }
}