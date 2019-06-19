package com.jeiker.zk.demo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 可重入锁-公平锁：售票机交替买票，性能低
 * User: jeikerxiao
 * Date: 2019/6/19 3:19 PM
 */
public class TestReentrantLock3 {

    // 有20张票可出售
    private static long tickets = 20;

    public static void main(String[] args) throws InterruptedException {
        // 可重入锁---公平锁
        ReentrantLock reentrantLock = new ReentrantLock(true);
        // 三个售票窗口
        Thread window1 = new Thread(new TestReentrantLock3.Runnable1(reentrantLock, "售票机1"));
        Thread window2 = new Thread(new TestReentrantLock3.Runnable1(reentrantLock, "售票机2"));
        Thread window3 = new Thread(new TestReentrantLock3.Runnable1(reentrantLock, "售票机3"));

        long time = System.currentTimeMillis();
        // 开始子线程
        window1.start();
        window2.start();
        window3.start();
        // 让父线程等待子线程结束之后才能继续运行。
        window1.join();
        window2.join();
        window3.join();

        System.out.println("车票售罄，耗时" + (System.currentTimeMillis() - time));
    }

    private static class Runnable1 implements Runnable {
        private final Lock lock;
        private String name;

        Runnable1(Lock lock, String name) {
            this.lock = lock;
            this.name = name;
        }

        @Override
        public void run() {
            while (tickets > 0) {
                try {
                    // 加锁
                    lock.lock();
                    if (tickets > 0) {
                        tickets--;
                        System.out.println(name + "卖出车票，余票" + tickets);
                    } else {
                        System.out.println(name + " 车票卖光了");
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 解锁
                    lock.unlock();
                }
            }
            System.out.println(name + "结束工作");
        }
    }
}