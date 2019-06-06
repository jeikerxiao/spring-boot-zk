package com.jeiker.zk.sync;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * Description: spring-boot-zk
 * User: jeikerxiao
 * Date: 2019/6/6 11:16 AM
 */
public class ApiCreate implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zookeeper = new ZooKeeper("127.0.0.1:32770", 5000, new ApiCreate());
        System.out.println(zookeeper.getState());
        connectedSemaphore.await();

        String path1 = zookeeper.create("/zk-test-ephemeral-", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("Success create znode: " + path1);

        String path2 = zookeeper.create("/zk-test-ephemeral-", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Success create znode: " + path2);
    }

    @Override
    public void process(WatchedEvent event) {
        if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
