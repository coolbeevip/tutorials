package com.coolbeevip.http;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.MatcherAssert.assertThat;

public class MyClientTest {
    ExecutorService executor = Executors.newFixedThreadPool(100);
    MyClient4X client = new MyClient4X();

    @Test
    public void test() throws IOException {
        doGet2();
    }

    @Test
    public void loopTest() {
        AtomicLong maxTime = new AtomicLong();
        AtomicLong minTime = new AtomicLong();
        AtomicLong totalTime = new AtomicLong();
        long total = 100;
        for (int i = 0; i < total; i++) {
            executor.execute(() -> {
                try {
                    long time = doGet3();
                    totalTime.addAndGet(time);
                    if (maxTime.get() < time) {
                        maxTime.set(time);
                    }
                    if (minTime.get() > time) {
                        minTime.set(time);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        try {
            executor.shutdown();
            executor.awaitTermination(500, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }

        System.out.println("minTime = " + minTime.get());
        System.out.println("maxTime = " + maxTime.get());
        System.out.println("avgTime = " + totalTime.get() / total);
        try {
            TimeUnit.SECONDS.sleep(600);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private long doGet() {
        long beginTime = System.currentTimeMillis();
        assertThat(client.get(), Matchers.is(200));
        return System.currentTimeMillis() - beginTime;
    }

    private long doGet2() throws IOException {
        long beginTime = System.currentTimeMillis();
        assertThat(client.get2(), Matchers.is(200));
        return System.currentTimeMillis() - beginTime;
    }

    private long doGet3() throws IOException {
        long beginTime = System.currentTimeMillis();
        assertThat(client.get3(), Matchers.is(200));
        return System.currentTimeMillis() - beginTime;
    }
}
