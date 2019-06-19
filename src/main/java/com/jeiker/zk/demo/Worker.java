package com.jeiker.zk.demo;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Description: spring-boot-zk
 * User: jeikerxiao
 * Date: 2019/6/19 10:25 AM
 */
public class Worker implements Runnable {

    private CountDownLatch downLatch;
    private String name;

    public Worker(CountDownLatch downLatch, String name) {
        this.downLatch = downLatch;
        this.name = name;
    }

    @Override
    public void run() {
        // Worker工作中
        this.doWork();
        try {
            TimeUnit.SECONDS.sleep(new Random().nextInt(10));
        } catch (InterruptedException ie) {
            System.out.println("Worker error:" + ie);
        }
        System.out.println(this.name + "活干完了！");
        // Worker工作完后，downLatch -1
        this.downLatch.countDown();
    }

    private void doWork() {
        System.out.println(this.name + "正在干活!");
    }

}