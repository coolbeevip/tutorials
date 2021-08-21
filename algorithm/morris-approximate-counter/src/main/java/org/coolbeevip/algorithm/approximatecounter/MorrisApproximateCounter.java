package org.coolbeevip.algorithm.approximatecounter;

import java.lang.invoke.MethodHandles;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 莫里斯近似计数算法
 * 使用一个 byte（数据范围 -128～127） 变量，实现千万级计数的近似计算
 *
 * https://en.wikipedia.org/wiki/Approximate_counting_algorithm
 * https://www.matongxue.com/madocs/12/
 */
public class MorrisApproximateCounter {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * 定义一个计数器变量
   */
  byte counter = 0;

  /**
   * 模拟抛硬币概率
   */
  Random random = new Random();

  /**
   * 返回计数值
   */
  public double get() {
    return Math.pow(Math.E, Math.log(2) * counter) - 1;
  }

  /**
   * 计数值累加
   */
  public byte increment() {
    double n_next = Math.pow(Math.E, Math.log(2) * counter+1) - 1;
    double n = Math.pow(Math.E, Math.log(2) * counter) - 1;
    double d = 1 / (n_next - n);
    if(random.nextDouble() < d){
      this.counter++;
    }
    return this.counter;
  }

  public static void main(String[] args) {
    MorrisApproximateCounter mc = new MorrisApproximateCounter();

    // 定义实际数量
    int realCount = 1000_000;

    double[][] real_graph_data = new double[realCount][2];
    double[][] approximate_graph_data = new double[realCount][2];

    for (int n = 0; n < realCount; n++) {
      // 累加计数
      mc.increment();

      real_graph_data[n][0] = n;
      real_graph_data[n][1] = n;

      approximate_graph_data[n][0] = n;
      approximate_graph_data[n][1] = mc.get();
    }

    // 输出实际计数 和 近似计数
    log.info("实际计数 {}, 近似计数 {}", realCount, (int) mc.get());

    // 绘制图形
    LineChartFrame chart = new LineChartFrame("Algorithm", "Morris Approximate Counting Algorithm",
        "n", "计数");
    chart.addXYSeries("实际", real_graph_data);
    chart.addXYSeries("估算", approximate_graph_data);
    chart.pack();
    chart.setVisible(true);
  }
}