package com.jeiker.zk.demo;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: ReentrantLock 的 条件
 * User: jeikerxiao
 * Date: 2019/6/19 3:22 PM
 */
public class TestReentrantLock4 {

    private static ReentrantLock lock = new ReentrantLock(true);
    // 一个与lock绑定，代表票已售空的Condition
    private static Condition noTicket = lock.newCondition();

    public static void main(String[] args) {
        // 售票系统中初始有5张车票
        TicketSystem tickets = new TicketSystem(5);
        // 三个售票窗口
        Thread window1 = new Thread(new TicketMachine(tickets, "售票机1"));
        Thread window2 = new Thread(new TicketMachine(tickets, "售票机2"));
        Thread window3 = new Thread(new TicketMachine(tickets, "售票机3"));
        // 新增车票
        Thread adder = new TicketsAdder(tickets);
        adder.start();
        // 售票线程
        window1.start();
        window2.start();
        window3.start();
    }

    private static class TicketSystem {
        // 车票数量
        private int num;

        TicketSystem(int num) {
            this.num = num;
            System.out.println("初始车票数量：" + num);
        }

        //车票余量
        public int getRemaining() {
            return num;
        }

        //卖出车票
        void sale() {
            num--;
        }

        //添加车票
        void add(int num) {
            this.num += num;
        }
    }

    /**
     * 售票窗口
     */
    private static class TicketMachine extends Thread {
        private final TicketSystem tickets;

        TicketMachine(TicketSystem tickets, String name) {
            this.setName(name);
            this.tickets = tickets;

        }

        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    if (tickets.getRemaining() > 0) {
                        tickets.sale();
                        System.out.println(getName() + "卖出车票，余票" + tickets.getRemaining());
                    } else {
                        System.out.println(getName() + " 车票卖光了,等待有新的车票放出。。。");
                        // 阻塞，等待系统中有足够的车票
                        noTicket.await();
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 新增车票的线程
     */
    private static class TicketsAdder extends Thread {
        private final TicketSystem tickets;
        private Random random = new Random();

        TicketsAdder(TicketSystem tickets) {
            this.tickets = tickets;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    // 随机添加车票
                    int ticketsNumber = random.nextInt(5) + 1;
                    tickets.add(ticketsNumber);
                    System.out.println("新增车票：" + ticketsNumber + ",余票" + tickets.getRemaining());
                    // 通知窗口已经有车票了
                    noTicket.signalAll();
                    lock.unlock();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}