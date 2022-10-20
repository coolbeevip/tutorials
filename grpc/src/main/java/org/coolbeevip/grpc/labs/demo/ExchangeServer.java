package org.coolbeevip.grpc.labs.demo;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author zhanglei
 */

public class ExchangeServer {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final int port;
  private final Server server;

  public ExchangeServer(int port) {
    this.port = port;
    this.server = NettyServerBuilder.forPort(port)
        .addService(new ExchangeService())
//        .channelType(selectorServerChannel())
//        .bossEventLoopGroup(selectorEventLoopGroup(1))
//        .workerEventLoopGroup(selectorEventLoopGroup(0))
        .channelType(NioServerSocketChannel.class)
        .bossEventLoopGroup(new NioEventLoopGroup(1))
        .workerEventLoopGroup(new NioEventLoopGroup())
        .build();
  }

  public Future<Void> start() {
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
      try {
        server.start();
        LOG.info("Server started, Listening on " + port);
        server.awaitTermination();
      } catch (Exception e) {

      }
    });
    return future;


  }

  public void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  private Class<? extends ServerChannel> selectorServerChannel() {
    if (OSInfo.isLinux()) {
      /**
       * RHEL/CentOS/Fedora:
       * sudo yum install autoconf automake libtool make tar \
       *                  glibc-devel libaio-devel \
       *                  libgcc.i686 glibc-devel.i686
       * Debian/Ubuntu:
       * sudo apt-get install autoconf automake libtool make tar \
       *                      gcc-multilib libaio-dev
       * */
      return EpollServerSocketChannel.class;
    } else if (OSInfo.isMacOS()) {
      /**
       * brew install autoconf automake libtool
       * */
      return KQueueServerSocketChannel.class;
    } else {
      return NioServerSocketChannel.class;
    }
  }

  private EventLoopGroup selectorEventLoopGroup(int nThreads) {
    if (OSInfo.isLinux()) {
      return new EpollEventLoopGroup(nThreads);
    } else if (OSInfo.isMacOS()) {
      return new KQueueEventLoopGroup(nThreads);
    } else {
      return new NioEventLoopGroup(nThreads);
    }
  }

}