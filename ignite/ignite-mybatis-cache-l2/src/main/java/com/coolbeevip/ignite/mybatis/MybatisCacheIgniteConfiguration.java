package com.coolbeevip.ignite.mybatis;

public class MybatisCacheIgniteConfiguration {

//  @Bean
//  IgniteConfiguration igniteConfiguration(){
//    IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
//    igniteConfiguration.setPeerClassLoadingEnabled(true);
//    igniteConfiguration.setClientMode(true);
//
//    TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
//    TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
//    ipFinder.setMulticastGroup("228.10.10.157");
//    ipFinder.setMulticastPort(47400);
//    ipFinder.setAddresses(Arrays.asList(
//        "10.1.207.180:48500",
//        "10.1.207.181:48500",
//        "10.1.207.182:48500"
//    ));
//    discoverySpi.setIpFinder(ipFinder);
//    igniteConfiguration.setDiscoverySpi(discoverySpi);
//
//    return igniteConfiguration;
//  }
//
//  @Bean("templateCacheCfg")
//  public CacheConfiguration templateCacheCfg(){
//    CacheConfiguration cacheConfiguration = new CacheConfiguration("myBatisCache");
//    cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
//    return cacheConfiguration;
//  }
}