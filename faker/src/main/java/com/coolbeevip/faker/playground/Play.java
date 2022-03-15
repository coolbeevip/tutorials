package com.coolbeevip.faker.playground;

import com.coolbeevip.faker.Host;
import com.coolbeevip.faker.Proc;
import com.coolbeevip.faker.core.Constants.RiskLevel;
import com.coolbeevip.faker.core.MetricsClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Play {

  private final MetricsClient client;
  private final WeightedCollection<RiskLevel> weightedCollection;
  ExecutorService executor = Executors.newFixedThreadPool(10);
  private List<Host> hosts = new ArrayList<>();
  private List<Proc> processes = new ArrayList<>();

  public Play(MetricsClient client, WeightedCollection weightedCollection) {
    this.client = client;
    this.weightedCollection = weightedCollection;
  }

  public void addHost(Host host) {
    this.hosts.add(host);
  }

  public void addProcess(Proc process) {
    this.processes.add(process);
  }

  public Future<Void> go(TimeUnit intervalUnit, int interval, TimeUnit durationUnit, int duration)
      throws ExecutionException, InterruptedException {
    long current = System.currentTimeMillis();

    Callable<Void> callableTask = () -> {
      while (System.currentTimeMillis() - current < durationUnit.toMillis(duration)) {
        try {
          RiskLevel riskLevel = weightedCollection.next();
          hosts.stream().forEach(host -> {
            try {
              this.client.push(host.take(riskLevel).id().type().timestamp().version(1).toJSON());
            } catch (JsonProcessingException e) {
              log.error("", e);
            }
          });
          processes.stream().forEach(process -> {
            try {
              this.client.push(process.take(riskLevel).id().type().timestamp().version(1).toJSON());
            } catch (JsonProcessingException e) {
              log.error("", e);
            }
          });
          intervalUnit.sleep(interval);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      return null;
    };

    return executor.submit(callableTask);
  }
}