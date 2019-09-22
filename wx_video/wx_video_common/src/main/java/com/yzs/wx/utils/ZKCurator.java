package com.yzs.wx.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZKCurator {


    private CuratorFramework client = null;

    final static Logger log = LoggerFactory.getLogger(ZKCurator.class);
    @Value("${zookeeper.server.url}")
    public String ZOOKEEPER_SERVER;

    public void init() {
        if (client != null) {
            return;
        }

        //创建重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);

        //创建zookeeper客户端
        client = CuratorFrameworkFactory.builder().connectString(ZOOKEEPER_SERVER)
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .namespace("admin")
                .build();

        client.start();

        try {
            if (client.checkExists().forPath("/bgm") == null) {

                client.create().creatingParentContainersIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/bgm");
                log.info("zookeeper初始化成功");

            }
        } catch (Exception e) {
            log.error("zookeeper初始化失败");
            e.printStackTrace();
        }

    }

    public void sendBgmOperator(String bgmId, String operObj) {
        try {

            client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)		// 节点类型：持久节点
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)			// acl：匿名权限
                    .forPath("/bgm/" + bgmId, operObj.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}