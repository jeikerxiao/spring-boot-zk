package com.jeiker.zk.sync;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * Description: spring-boot-zk
 * User: jeikerxiao
 * Date: 2019/6/6 11:30 AM
 */
public class ApiDelete implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk;

    public static void main(String[] args) throws Exception {
        String path = "/zk-book";
        zk = new ZooKeeper("127.0.0.1:32770", 5000,
                new ApiDelete());
        connectedSemaphore.await();

        zk.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("success create znode: " + path);
        zk.create(path + "/c1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("success create znode: " + path + "/c1");
        try {
            zk.delete(path, -1);
        } catch (Exception e) {
            System.out.println("fail to delete znode: " + path);
        }

        zk.delete(path + "/c1", -1);
        System.out.println("success delete znode: " + path + "/c1");
        zk.delete(path, -1);
        System.out.println("success delete znode: " + path);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            if (Event.EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            }
        }
    }
}
