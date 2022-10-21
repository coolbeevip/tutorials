package com.coolbeevip.ignite.common;

import org.apache.ignite.lifecycle.LifecycleBean;

import java.util.Collection;

public class IgniteNodeFactory {

  public static IgniteNode createIgniteNode(boolean clientMode, String multicastGroup) {
    IgniteNodeConfig config = new IgniteNodeConfig(clientMode, multicastGroup,
        new IgniteNodeLifecycle());
    return new IgniteNode(config);
  }

  public static IgniteNode createIgniteNode(boolean clientMode, String multicastGroup,
                                            LifecycleBean... lifecycleBeans) {
    IgniteNodeConfig config = new IgniteNodeConfig(clientMode, multicastGroup, lifecycleBeans);
    return new IgniteNode(config);
  }

  public static IgniteNode createIgniteNode(boolean clientMode, String localAddress, int localPort,
                                            int localPortRange,
                                            Collection<String> addresses) {
    IgniteNodeConfig config = new IgniteNodeConfig(clientMode, localAddress, localPort,
        localPortRange, addresses,
        new IgniteNodeLifecycle());
    return new IgniteNode(config);
  }

  public static IgniteNode createIgniteNode(boolean clientMode, String localAddress, int localPort,
                                            int localPortRange,
                                            Collection<String> addresses,
                                            LifecycleBean... lifecycleBeans) {
    IgniteNodeConfig config = new IgniteNodeConfig(clientMode, localAddress, localPort,
        localPortRange, addresses,
        lifecycleBeans);
    return new IgniteNode(config);
  }

  public static IgniteNode createIgniteNode(boolean clientMode, String localAddress, int localPort,
                                            int localPortRange,
                                            Collection<String> addresses, String keystoreFile,
                                            String keystorePass, String truststoreFile, String truststorePass) {
    IgniteNodeConfig config = new IgniteNodeConfig(clientMode, localAddress, localPort,
        localPortRange, addresses,
        keystoreFile, keystorePass,
        truststoreFile, truststorePass, new IgniteNodeLifecycle());
    return new IgniteNode(config);
  }
}