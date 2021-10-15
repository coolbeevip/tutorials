package com.coolbeevip.ignite;

import java.util.Collection;
import org.apache.ignite.lifecycle.LifecycleBean;

public class IgniteNodeFactory {

  public static IgniteNode createIgniteNode(String multicastGroup) {
    IgniteNodeConfig config = new IgniteNodeConfig(multicastGroup, new IgniteNodeLifecycle());
    return new IgniteNode(config);
  }

  public static IgniteNode createIgniteNode(String multicastGroup,
      LifecycleBean... lifecycleBeans) {
    IgniteNodeConfig config = new IgniteNodeConfig(multicastGroup, lifecycleBeans);
    return new IgniteNode(config);
  }

  public static IgniteNode createIgniteNode(String localAddress, int localPort, int localPortRange,
      Collection<String> addrs) {
    IgniteNodeConfig config = new IgniteNodeConfig(localAddress, localPort, localPortRange, addrs,
        new IgniteNodeLifecycle());
    return new IgniteNode(config);
  }

  public static IgniteNode createIgniteNode(String localAddress, int localPort, int localPortRange,
      Collection<String> addrs,
      LifecycleBean... lifecycleBeans) {
    IgniteNodeConfig config = new IgniteNodeConfig(localAddress, localPort, localPortRange, addrs,
        lifecycleBeans);
    return new IgniteNode(config);
  }

  public static IgniteNode createIgniteNode(String localAddress, int localPort, int localPortRange,
      Collection<String> addrs, String keystoreFile,
      String keystorePass, String truststoreFile, String truststorePass) {
    IgniteNodeConfig config = new IgniteNodeConfig(localAddress, localPort, localPortRange, addrs,
        keystoreFile, keystorePass,
        truststoreFile, truststorePass, new IgniteNodeLifecycle());
    return new IgniteNode(config);
  }
}