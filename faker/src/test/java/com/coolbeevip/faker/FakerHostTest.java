package com.coolbeevip.faker;

import com.coolbeevip.faker.core.Constants.RiskLevel;
import com.coolbeevip.faker.core.MetricsClient;
import com.coolbeevip.faker.core.MetricsClientLog;
import com.coolbeevip.faker.playground.Play;
import com.coolbeevip.faker.playground.WeightedCollection;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FakerHostTest {

  @Test
  @SneakyThrows
  public void test() {
    WeightedCollection<RiskLevel> weightedCollection = new WeightedCollection();
    weightedCollection.add(2,RiskLevel.LOW);
    weightedCollection.add(5,RiskLevel.MID);
    weightedCollection.add(2,RiskLevel.NORMAL);
    weightedCollection.add(1,RiskLevel.HIGH);

    MetricsClient client = new MetricsClientLog();
    Play play = new Play(client, weightedCollection);

    Faker faker = new Faker();

    Host host1 = faker.host("192.168.0.1", 16, 976490576, 2033396);
    play.addHost(host1);

    Proc processWeb = faker.process("app-web", 65535, new BigInteger("8589934592"), host1);
    play.addProcess(processWeb);

    Host host2 = faker.host("192.168.0.1", 16, 976490576, 2033396);
    play.addHost(host2);

    Proc processBackend = faker.process("app-backend", 65535, new BigInteger("8589934592"),
        host2);
    play.addProcess(processBackend);

    play.go(TimeUnit.SECONDS, 5, TimeUnit.MINUTES, 10);
  }
}
