package com.jeiker.zk.demo;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 可重入锁
 * User: jeikerxiao
 * Date: 2019/6/19 2:57 PM
 */
public class TestReentrantLock {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();

        new Thread(() -> {
            System.out.println(lock.getHoldCount());
            lock.lock();
            System.out.println(lock.getHoldCount());
            lock.lock();
            System.out.println(lock.getHoldCount());

            lock.unlock();
            System.out.println(lock.getHoldCount());
            lock.unlock();
            System.out.println(lock.getHoldCount());
        }).start();
    }
}
