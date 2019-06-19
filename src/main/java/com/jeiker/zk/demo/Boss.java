package com.jeiker.zk.demo;

import java.util.concurrent.CountDownLatch;

/**
 * Description: spring-boot-zk
 * User: jeikerxiao
 * Date: 2019/6/19 10:25 AM
 */
public class Boss implements Runnable {

    private CountDownLatch downLatch;

    public Boss(CountDownLatch downLatch) {
        this.downLatch = downLatch;
    }

    @Override
    public void run() {
        System.out.println("老板正在等所有的工人干完活......");
        try {
            // Boss 等待 downLatch == 0,然后往下执行
            this.downLatch.await();
        } catch (InterruptedException ie) {
            System.out.println("Worker error:" + ie);
        }
        System.out.println("工人活都干完了，老板开始检查了！");
    }
}