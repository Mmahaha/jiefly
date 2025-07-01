package com.example.zookeeper.registry;

import com.example.zookeeper.util.ZKClientUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务注册类
 * 用于将服务信息注册到ZooKeeper中
 */
public class ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    
    // 服务注册的根路径
    private static final String REGISTRY_PATH = "/services";
    
    private final CuratorFramework client;
    
    public ServiceRegistry() {
        this.client = ZKClientUtil.getClient();
        createRootIfNotExists();
    }
    
    /**
     * 创建根节点，如果不存在的话
     */
    private void createRootIfNotExists() {
        try {
            if (client.checkExists().forPath(REGISTRY_PATH) == null) {
                client.create()
                      .creatingParentsIfNeeded()
                      .withMode(CreateMode.PERSISTENT)
                      .forPath(REGISTRY_PATH, "services root".getBytes());
                logger.info("创建服务注册根节点: {}", REGISTRY_PATH);
            }
        } catch (Exception e) {
            logger.error("创建服务注册根节点失败", e);
            throw new RuntimeException("创建服务注册根节点失败", e);
        }
    }
    
    /**
     * 注册服务
     * @param serviceName 服务名称
     * @param serviceAddress 服务地址 (IP:PORT)
     */
    public void register(String serviceName, String serviceAddress) {
        String servicePath = REGISTRY_PATH + "/" + serviceName;
        try {
            // 创建服务节点（持久节点）
            if (client.checkExists().forPath(servicePath) == null) {
                client.create()
                      .creatingParentsIfNeeded()
                      .withMode(CreateMode.PERSISTENT)
                      .forPath(servicePath, "service".getBytes());
                logger.info("创建服务节点: {}", servicePath);
            }
            
            // 创建地址节点（临时节点，会话断开自动删除）
            String addressPath = servicePath + "/" + serviceAddress;
            client.create()
                  .withMode(CreateMode.EPHEMERAL)
                  .forPath(addressPath, serviceAddress.getBytes());
            
            logger.info("注册服务成功: {}，地址: {}", serviceName, serviceAddress);
        } catch (Exception e) {
            logger.error("注册服务失败: " + serviceName, e);
            throw new RuntimeException("注册服务失败: " + serviceName, e);
        }
    }
    
    /**
     * 注销服务
     * @param serviceName 服务名称
     * @param serviceAddress 服务地址
     */
    public void unregister(String serviceName, String serviceAddress) {
        try {
            String addressPath = REGISTRY_PATH + "/" + serviceName + "/" + serviceAddress;
            if (client.checkExists().forPath(addressPath) != null) {
                client.delete().forPath(addressPath);
                logger.info("注销服务成功: {}，地址: {}", serviceName, serviceAddress);
            }
        } catch (Exception e) {
            logger.error("注销服务失败: " + serviceName, e);
        }
    }
}