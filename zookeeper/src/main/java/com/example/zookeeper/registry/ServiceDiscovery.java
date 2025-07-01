package com.example.zookeeper.registry;

import com.example.zookeeper.util.ZKClientUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务发现类
 * 用于发现ZooKeeper中注册的服务
 */
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
    
    // 服务注册的根路径
    private static final String REGISTRY_PATH = "/services";
    
    private final CuratorFramework client;
    private final Random random = new Random();
    
    // 本地缓存，存储服务名称到服务地址列表的映射
    private final ConcurrentMap<String, List<String>> serviceAddressMap = new ConcurrentHashMap<>();
    
    public ServiceDiscovery() {
        this.client = ZKClientUtil.getClient();
    }
    
    /**
     * 发现服务，并监听服务地址变化
     * @param serviceName 服务名称
     */
    public void discover(String serviceName) {
        String servicePath = REGISTRY_PATH + "/" + serviceName;
        try {
            // 检查服务节点是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                logger.warn("服务不存在: {}", serviceName);
                return;
            }
            
            // 获取服务地址列表
            List<String> addressList = client.getChildren().forPath(servicePath);
            updateServiceAddressMap(serviceName, addressList);
            
            // 监听服务地址变化
            watchServiceNode(serviceName, servicePath);
            
            logger.info("发现服务: {}，地址列表: {}", serviceName, addressList);
        } catch (Exception e) {
            logger.error("发现服务失败: " + serviceName, e);
            throw new RuntimeException("发现服务失败: " + serviceName, e);
        }
    }
    
    /**
     * 监听服务节点变化
     * @param serviceName 服务名称
     * @param servicePath 服务路径
     */
    private void watchServiceNode(String serviceName, String servicePath) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, servicePath, true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED ||
                    event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED ||
                    event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                    // 服务地址列表发生变化，更新本地缓存
                    List<String> addressList = client.getChildren().forPath(servicePath);
                    updateServiceAddressMap(serviceName, addressList);
                    logger.info("服务地址列表已更新: {}，地址列表: {}", serviceName, addressList);
                }
            }
        });
        pathChildrenCache.start();
    }
    
    /**
     * 更新服务地址映射表
     * @param serviceName 服务名称
     * @param addressList 地址列表
     */
    private void updateServiceAddressMap(String serviceName, List<String> addressList) {
        serviceAddressMap.put(serviceName, new ArrayList<>(addressList));
    }
    
    /**
     * 获取服务地址
     * @param serviceName 服务名称
     * @return 服务地址，如果没有可用的服务，返回null
     */
    public String getServiceAddress(String serviceName) {
        List<String> addressList = serviceAddressMap.get(serviceName);
        if (addressList == null || addressList.isEmpty()) {
            // 如果本地缓存中没有，尝试重新发现
            discover(serviceName);
            addressList = serviceAddressMap.get(serviceName);
            if (addressList == null || addressList.isEmpty()) {
                return null;
            }
        }
        
        // 负载均衡，随机选择一个地址
        return addressList.get(random.nextInt(addressList.size()));
    }
}