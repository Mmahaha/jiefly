package com.example.zookeeper.config;

import com.example.zookeeper.util.ZKClientUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置中心
 * 基于ZooKeeper实现的配置管理，支持动态更新配置
 */
public class ConfigCenter {
    private static final Logger logger = LoggerFactory.getLogger(ConfigCenter.class);
    
    // 配置根路径
    private static final String CONFIG_PATH = "/configs";
    
    private final CuratorFramework client;
    
    // 本地配置缓存
    private final Map<String, String> configCache = new ConcurrentHashMap<>();
    
    // 配置变更监听器集合
    private final Map<String, ConfigChangeListener> listeners = new HashMap<>();
    
    public ConfigCenter() {
        this.client = ZKClientUtil.getClient();
        createRootIfNotExists();
    }
    
    /**
     * 创建根节点，如果不存在的话
     */
    private void createRootIfNotExists() {
        try {
            if (client.checkExists().forPath(CONFIG_PATH) == null) {
                client.create()
                      .creatingParentsIfNeeded()
                      .withMode(CreateMode.PERSISTENT)
                      .forPath(CONFIG_PATH, "configs root".getBytes());
                logger.info("创建配置中心根节点: {}", CONFIG_PATH);
            }
        } catch (Exception e) {
            logger.error("创建配置中心根节点失败", e);
            throw new RuntimeException("创建配置中心根节点失败", e);
        }
    }
    
    /**
     * 设置配置项
     * @param key 配置键
     * @param value 配置值
     */
    public void setConfig(String key, String value) {
        String configPath = CONFIG_PATH + "/" + key;
        try {
            byte[] valueBytes = value.getBytes();
            if (client.checkExists().forPath(configPath) == null) {
                client.create()
                      .creatingParentsIfNeeded()
                      .withMode(CreateMode.PERSISTENT)
                      .forPath(configPath, valueBytes);
                logger.info("创建配置项: {}, 值: {}", key, value);
            } else {
                client.setData().forPath(configPath, valueBytes);
                logger.info("更新配置项: {}, 值: {}", key, value);
            }
            
            // 更新本地缓存
            configCache.put(key, value);
        } catch (Exception e) {
            logger.error("设置配置项失败: " + key, e);
            throw new RuntimeException("设置配置项失败: " + key, e);
        }
    }
    
    /**
     * 获取配置项
     * @param key 配置键
     * @return 配置值，如果不存在则返回null
     */
    public String getConfig(String key) {
        // 先从本地缓存获取
        if (configCache.containsKey(key)) {
            return configCache.get(key);
        }
        
        // 本地缓存没有，从ZooKeeper获取
        String configPath = CONFIG_PATH + "/" + key;
        try {
            if (client.checkExists().forPath(configPath) != null) {
                byte[] valueBytes = client.getData().forPath(configPath);
                String value = new String(valueBytes);
                
                // 更新本地缓存
                configCache.put(key, value);
                
                return value;
            }
        } catch (Exception e) {
            logger.error("获取配置项失败: " + key, e);
        }
        return null;
    }
    
    /**
     * 删除配置项
     * @param key 配置键
     */
    public void deleteConfig(String key) {
        String configPath = CONFIG_PATH + "/" + key;
        try {
            if (client.checkExists().forPath(configPath) != null) {
                client.delete().forPath(configPath);
                logger.info("删除配置项: {}", key);
                
                // 从本地缓存中移除
                configCache.remove(key);
            }
        } catch (Exception e) {
            logger.error("删除配置项失败: " + key, e);
            throw new RuntimeException("删除配置项失败: " + key, e);
        }
    }
    
    /**
     * 监听配置变化
     * @param key 配置键
     * @param listener 配置变更监听器
     */
    public void watchConfig(String key, ConfigChangeListener listener) {
        String configPath = CONFIG_PATH + "/" + key;
        try {
            // 确保配置存在
            if (client.checkExists().forPath(configPath) == null) {
                logger.warn("监听的配置项不存在: {}", key);
                return;
            }
            
            // 创建节点缓存
            NodeCache nodeCache = new NodeCache(client, configPath);
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    if (nodeCache.getCurrentData() != null) {
                        String newValue = new String(nodeCache.getCurrentData().getData());
                        String oldValue = configCache.get(key);
                        
                        // 更新本地缓存
                        configCache.put(key, newValue);
                        
                        // 通知监听器
                        listener.configChanged(key, oldValue, newValue);
                        logger.info("配置项已变更: {}, 旧值: {}, 新值: {}", key, oldValue, newValue);
                    }
                }
            });
            nodeCache.start();
            
            // 保存监听器引用
            listeners.put(key, listener);
            
            logger.info("开始监听配置项: {}", key);
        } catch (Exception e) {
            logger.error("监听配置项失败: " + key, e);
            throw new RuntimeException("监听配置项失败: " + key, e);
        }
    }
    
    /**
     * 配置变更监听器接口
     */
    public interface ConfigChangeListener {
        /**
         * 配置变更回调方法
         * @param key 配置键
         * @param oldValue 旧值
         * @param newValue 新值
         */
        void configChanged(String key, String oldValue, String newValue);
    }
}