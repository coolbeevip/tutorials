package com.coolbeevip.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterMetrics;

public class IgniteNode {

  Ignite ignite;

  static String INFO_FMT = "%s [cluster nodes %d, cpus %d, memory %d, waiting jobs %d, job execute avg time %f]";

  public IgniteNode(IgniteNodeConfig config) {
    ignite = Ignition.start(config.getConfiguration());

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        ignite.close();
      }
    });
  }

  public String info() {
    ClusterMetrics metrics = ignite.cluster().metrics();
    return String.format(INFO_FMT, ignite.cluster().localNode().consistentId(), metrics.getTotalNodes(), metrics.getTotalCpus(),
        metrics.getHeapMemoryTotal(), metrics.getCurrentWaitingJobs(),
        metrics.getAverageJobExecuteTime());
  }


}