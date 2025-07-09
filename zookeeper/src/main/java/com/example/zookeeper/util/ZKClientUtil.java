package com.example.zookeeper.util;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZooKeeper客户端工具类，提供创建Curator客户端的方法
 */
public class ZKClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(ZKClientUtil.class);
    
    // ZooKeeper服务器地址，实际使用时应该配置到配置文件中
    private static final String ZK_CONNECTION_STRING = "192.168.3.5:2181";
    // 会话超时时间
    private static final int SESSION_TIMEOUT_MS = 999999999;
    // 连接超时时间
    private static final int CONNECTION_TIMEOUT_MS = 15000;
    // 命名空间，所有操作都在该命名空间下进行
    private static final String NAMESPACE = "jay";
    
    private static CuratorFramework client;
    
    /**
     * 获取Curator客户端实例
     * @return CuratorFramework实例
     */
    public static synchronized CuratorFramework getClient() {
        if (client == null) {
            // 重试策略: 初始休眠时间为1000ms, 最大重试次数为3
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            
            // 创建客户端
            client = CuratorFrameworkFactory.builder()
                    .connectString(ZK_CONNECTION_STRING)
                    .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                    .connectionTimeoutMs(CONNECTION_TIMEOUT_MS)
                    .retryPolicy(retryPolicy)
                    .namespace(NAMESPACE)
                    .build();
            
            // 启动客户端
            client.start();
            logger.info("ZooKeeper客户端已启动，连接到: {}", ZK_CONNECTION_STRING);
        }
        return client;
    }
    
    /**
     * 关闭Curator客户端
     */
    public static void closeClient() {
        if (client != null) {
            client.close();
            client = null;
            logger.info("ZooKeeper客户端已关闭");
        }
    }
}