package com.coolbeevip.flyway;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

public class FlywayTestManual {

  String url="jdbc:mysql:loadbalance://192.168.51.206:3810,192.168.51.207:3810/nc_notifier?roundRobinLoadBalance=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&allowMultiQueries=true&allowPublicKeyRetrieval=true";
  String user="user";
  String password="pass";

  @Test
  public void test(){
    Flyway flyway = Flyway.configure()
      .locations("classpath:/db/mysql")
      .baselineOnMigrate(true)
      .group(true) // loadbalance 模式必须设置 group = true
      .dataSource(url, user, password).load();
    flyway.migrate();
  }
}
