package com.coolbeevip.cpu;

/**
 * 根据二维数组在内存中的存储结构特性，演示 CPU L1 对于连续内存访问的优化特性
 */
public class CacheLineEffect {

  static int ARRAY_X = 1024 * 1024;
  static int ARRAY_Y = 8;

  public static void main(String[] args) {
    // Define array & init
    long[][] array = new long[ARRAY_X][];
    for (int i = 0; i < ARRAY_X; i++) {
      array[i] = new long[ARRAY_Y];
      for (int j = 0; j < ARRAY_Y; j++) {
        array[i][j] = 0L;
      }
    }

    System.out.println("Fast Loop Times:" + fastLoop(array) + "ms");
    System.out.println("Slow Loop Times:" + slowLoop(array) + "ms");
  }

  private static long fastLoop(long[][] array) {
    long beginTime = System.currentTimeMillis();
    for (int x = 0; x < ARRAY_X; x += 1) {
      for (int y = 0; y < ARRAY_Y; y++) {
        long value = array[x][y];
      }
    }
    return System.currentTimeMillis() - beginTime;
  }

  private static long slowLoop(long[][] array) {
    long beginTime = System.currentTimeMillis();
    for (int y = 0; y < ARRAY_Y; y += 1) {
      for (int x = 0; x < ARRAY_X; x++) {
        long value = array[x][y];
      }
    }
    return System.currentTimeMillis() - beginTime;
  }
}