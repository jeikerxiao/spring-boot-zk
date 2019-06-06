package com.jeiker.zk.service;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * Description: spring-boot-zk
 * User: jeikerxiao
 * Date: 2019/6/5 2:34 PM
 */
public class CuratorTest {

    public static void main(String[] args) throws Exception {

        // 多个服务器用逗号分隔
        String zkUrl = "localhost:32770";

        // 重连server机制, 最多重连3次, 每次重连间隔会加长, 初次重连的间隔为1000毫秒
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        /**
         * 重连机制有: ExponentialBackoffRetry: 重试指定的次数, 且每一次重试之间停顿的时间逐渐增加 RetryNtimes:
         * 指定最大重试次数的重试策略 RetryOneTimes: 仅重试一次 RetryUntilElapsed:一直重试直到达到规定的时间
         */

        // CuratorFramework 是一个线程安全的类, 可以完成zk所有操作
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkUrl, retryPolicy);

        // 打开客户端
        client.start();

        // 检查节点是否存在
        if (client.checkExists().forPath("/head") != null) {
            // 删除节点
            client.delete().forPath("/head");
        }

        // 新建节点, 节点数据为空
        client.create().forPath("/head", new byte[0]);

        // 重新赋值,需要将String转成 byte 数组
        client.setData().forPath("/head", "ABC".getBytes());

        // 获取节点的版本等信息, 返回一个 Stat实例
        Stat stat = client.checkExists().forPath("/head");
        System.out.println(stat.getCzxid());

        // 获取节点值, getData().forPath()结果为 byte 数组, 可以转成String类型
        String value = new String(client.getData().forPath("/head"));
        System.out.println(String.format("/head data is :%s", value));

        // 创建临时序列节点
        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/head/child");

        if (client.checkExists().forPath("/a") != null) {
            // 级联删除节点
            client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/a");
        }

        // 级联创建节点, 创建之前不需要检查上级节点是否存在
        client.create().creatingParentsIfNeeded().forPath("/a/b/c");

        client.close();
    }
}
