package com.jeiker.zk.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: spring-boot-zk
 * User: jeikerxiao
 * Date: 2019/6/19 10:26 AM
 */
public class TestCountDownLatch {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newCachedThreadPool();

        CountDownLatch latch = new CountDownLatch(3);

        Worker w1 = new Worker(latch, "工人1");
        Worker w2 = new Worker(latch, "工人2");
        Worker w3 = new Worker(latch, "工人3");

        Boss boss = new Boss(latch);

        executor.execute(w3);
        executor.execute(w2);
        executor.execute(w1);
        executor.execute(boss);

        executor.shutdown();
    }
}
