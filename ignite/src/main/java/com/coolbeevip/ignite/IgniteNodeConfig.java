package com.coolbeevip.ignite;

import java.util.Collection;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.ssl.SslContextFactory;

public class IgniteNodeConfig {

  IgniteConfiguration configuration = new IgniteConfiguration();

  public IgniteNodeConfig(String multicastGroup, LifecycleBean... lifecycleBeans) {
    commonConfig();
    this.configuration.setClientMode(false);
    this.configuration.setLifecycleBeans(lifecycleBeans);
    this.configuration.setDiscoverySpi(discoveryMulticastSpi(multicastGroup));
  }

  public IgniteNodeConfig(String localAddress, int localPort, int localPortRange,
      Collection<String> addrs, LifecycleBean... lifecycleBeans) {
    commonConfig();
    this.configuration.setClientMode(false);
    this.configuration.setLifecycleBeans(lifecycleBeans);
    this.configuration
        .setDiscoverySpi(discoveryVmIpSpi(localAddress, localPort, localPortRange, addrs));
  }

  public IgniteNodeConfig(String localAddress, int localPort, int localPortRange,
      Collection<String> addrs, String keystoreFile, String keystorePass,
      String truststoreFile, String truststorePass, LifecycleBean... lifecycleBeans) {
    commonConfig();
    this.configuration.setClientMode(false);
    this.configuration.setLifecycleBeans(lifecycleBeans);
    this.configuration
        .setDiscoverySpi(discoveryVmIpSpi(localAddress, localPort, localPortRange, addrs));
    this.setSsl(keystoreFile, keystorePass, truststoreFile, truststorePass);
  }

  /**
   * 设置 KeyStore用于服务器认证服务端（不要泄漏），TrustStore用于客户端认证服务器
   *
   * keytool 生成 server.jks 和 trust.jks
   */
  public void setSsl(String keystoreFile, String keystorePass, String truststoreFile,
      String truststorePass) {
    SslContextFactory factory = new SslContextFactory();
    factory.setKeyStoreFilePath(keystoreFile);
    factory.setKeyStorePassword(keystorePass.toCharArray());
    factory.setTrustStoreFilePath(truststoreFile);
    factory.setTrustStorePassword(truststorePass.toCharArray());
    //factory.setProtocol("SSL");
    factory.setProtocol("TLS");
    this.configuration.setSslContextFactory(factory);
  }

  private void commonConfig() {
    // 设置故障检测超
    this.configuration.setFailureDetectionTimeout(5_000);
    // 禁用分布式类加载器（开启后可通过节点间的字节码交换实现自动在每个节点加载需要的类，当你需要一个可控的节点时，不建议启用）
    this.configuration.setPeerClassLoadingEnabled(false);
  }

  private TcpDiscoverySpi discoveryMulticastSpi(String multicastGroup) {
    TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
    TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
    ipFinder.setMulticastGroup(multicastGroup);
    discoverySpi.setIpFinder(ipFinder);
    return discoverySpi;
  }

  private TcpDiscoverySpi discoveryVmIpSpi(String localAddress, int localPort, int localPortRange,
      Collection<String> addresses) {
    TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
    TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
    ipFinder.setAddresses(addresses);

    discoverySpi.setIpFinder(ipFinder);
    discoverySpi.setLocalAddress(localAddress);
    discoverySpi.setLocalPort(localPort);
    discoverySpi.setLocalPortRange(localPortRange);
    return discoverySpi;
  }

  public IgniteConfiguration getConfiguration() {
    return configuration;
  }
}