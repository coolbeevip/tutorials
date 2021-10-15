package com.coolbeevip.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicLong;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.configuration.CacheConfiguration;

public class IgniteNode implements AutoCloseable {

  Ignite ignite;

  static String INFO_FMT = "%s %s [servers %d, clients %d, cpus %d, memory %d, waiting jobs %d, job execute avg time %f]";

  public IgniteNode(IgniteNodeConfig config) {
    ignite = Ignition.start(config.getConfiguration());

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        ignite.close();
      }
    });
  }

  public String getId() {
    return ignite.cluster().localNode().id().toString();
  }

  public String info() {
    ClusterMetrics metrics = ignite.cluster().metrics();
    return String.format(INFO_FMT, ignite.cluster().localNode().id().toString(),
        ignite.cluster().localNode().consistentId(),
        ignite.cluster().forServers().nodes().size(),
        ignite.cluster().nodes().size() - ignite.cluster().forServers().nodes().size(),
        metrics.getTotalCpus(),
        metrics.getHeapMemoryTotal(), metrics.getCurrentWaitingJobs(),
        metrics.getAverageJobExecuteTime());
  }

  public void close() {
    ignite.close();
  }

  public IgniteCache getCreateCache(String name, CacheMode cacheMode, int backups,
      CacheWriteSynchronizationMode writeSync) {
    CacheConfiguration cacheConfiguration = new CacheConfiguration();
    cacheConfiguration.setName(name);
    cacheConfiguration.setCacheMode(cacheMode);
    cacheConfiguration.setBackups(backups);
    cacheConfiguration.setWriteSynchronizationMode(writeSync);
    cacheConfiguration.setAtomicityMode(CacheAtomicityMode.ATOMIC);
    return ignite.getOrCreateCache(cacheConfiguration);
  }


  public IgniteAtomicLong createAtomicLong(String name) {
    return ignite.atomicLong(name, 0, true);
  }

  public IgniteAtomicLong createAtomicLong(String name, long initialValue) {
    return ignite.atomicLong(name, initialValue, true);
  }

  public IgniteCache getCache(String name) {
    return ignite.cache(name);
  }

  public void destroyCache(String name) {
    ignite.destroyCache(name);
  }
}