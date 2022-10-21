package com.coolbeevip.faker;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import java.math.BigDecimal;
import java.math.BigInteger;

@JsonIgnoreType
public class Faker {

  public Faker() {

  }

  public Host host(String ip, int cpuNum, long diskTotalSpace,
                   long memoryTotal) {
    return new Host(this, ip, cpuNum, diskTotalSpace, memoryTotal);
  }

  public Jvm jvm(BigInteger memoryMax) {
    return new Jvm(this, memoryMax);
  }

  public Proc process(String name, long filesMax, BigInteger jvmMemoryMax, Host host) {
    Proc process = new Proc(this, name, filesMax, jvmMemoryMax, host);
    host.deployProcess(process);
    return process;
  }

  public double randomPercentage(long min, long max) {
    double value = (min + Math.random() * (max - min)) / 100;
    return BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
  }

  public double calcPercentage(BigInteger use, BigInteger total) {
    BigDecimal useBigDecimal = new BigDecimal(use);
    BigDecimal totalBigDecimal = new BigDecimal(total);
    return useBigDecimal.divide(totalBigDecimal)
        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
  }

  public long randomLong(long min, long max) {
    return (long) (min + Math.random() * (max - min));
  }

  public BigInteger randomBigInteger(BigInteger min, BigInteger max) {
    return min.add(max.subtract(min).divide(BigInteger.valueOf(100))
        .multiply(BigInteger.valueOf((long) (Math.random() * 100))));
  }
}