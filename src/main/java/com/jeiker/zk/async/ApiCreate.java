package com.jeiker.zk.async;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * Description: 异步创建API
 * User: jeikerxiao
 * Date: 2019/6/6 11:22 AM
 */
public class ApiCreate implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zookeeper = new ZooKeeper("127.0.0.1:32770", 5000, new ApiCreate());
        System.out.println(zookeeper.getState());
        connectedSemaphore.await();

        zookeeper.create("/zk-test-ephemeral-", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                new AsyncCallback.StringCallback() {
                    @Override
                    public void processResult(int i, String s, Object o, String s1) {
                        System.out.println("Create path result: [" + i + ", " + s + ", " + o + ", real path name: " + s1);
                    }
                }, "I am context. ");

        zookeeper.create("/zk-test-ephemeral-", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                new AsyncCallback.StringCallback() {
                    @Override
                    public void processResult(int i, String s, Object o, String s1) {
                        System.out.println("Create path result: [" + i + ", " + s + ", " + o + ", real path name: " + s1);
                    }
                }, "I am context. ");

        zookeeper.create("/zk-test-ephemeral-", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
                (i, s, o, s1) -> {
                    System.out.println("Create path result: [" + i + ", " + s + ", " + o + ", real path name: " + s1);
                }, "I am context. ");
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
