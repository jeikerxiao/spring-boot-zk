package com.jeiker.zk.sync;

import org.apache.zookeeper.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Description: spring-boot-zk
 * User: jeikerxiao
 * Date: 2019/6/6 11:40 AM
 */
public class ApiGet implements Watcher {

    // 原子计算器
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;

    public static void main(String[] args) throws Exception {

        String path = "/zk-book-1";
        zk = new ZooKeeper("127.0.0.1:32770", 5000, new ApiGet());
        connectedSemaphore.await();
        // 创建永久父节点
        zk.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("success create znode: " + path);
        // 创建临时子节点c1
        zk.create(path + "/c1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("success create znode: " + path + "/c1");

        List<String> childrenList = zk.getChildren(path, true);
        System.out.println(childrenList);
        // 创建临时子节点c2
        zk.create(path + "/c2", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("success create znode: " + path + "/c2");
        Thread.sleep(1000);
        // 创建临时子节点c3
        zk.create(path + "/c3", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("success create znode: " + path + "/c3");
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            if (Event.EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
                try {
                    System.out.println("ReGet Child:" + zk.getChildren(event.getPath(), true));
                } catch (Exception e) {
                    System.out.println("watcher error:" + e);
                }
            }
        }
    }
}
