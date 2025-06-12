package org.example.redis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis配置类，负责管理Redis连接
 */
public class RedisConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    
    // 默认Redis配置
    private static final String DEFAULT_HOST = "192.168.3.5";
    private static final int DEFAULT_PORT = 6379;
    private static final String DEFAULT_PASSWORD = null; // 无密码
    private static final int DEFAULT_DATABASE = 0;
    private static final int DEFAULT_TIMEOUT = 2000; // 连接超时时间（毫秒）
    
    // 连接池配置
    private static final int MAX_TOTAL = 50; // 最大连接数
    private static final int MAX_IDLE = 10; // 最大空闲连接数
    private static final int MIN_IDLE = 5;  // 最小空闲连接数
    private static final long MAX_WAIT_MILLIS = 3000; // 获取连接最大等待时间（毫秒）
    private static final boolean TEST_ON_BORROW = true; // 获取连接时测试连接是否可用
    
    private static JedisPool jedisPool;
    
    static {
        initJedisPool(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_PASSWORD, DEFAULT_DATABASE, DEFAULT_TIMEOUT);
    }
    
    /**
     * 初始化Redis连接池
     */
    public static void initJedisPool(String host, int port, String password, int database, int timeout) {
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(MAX_TOTAL);
            poolConfig.setMaxIdle(MAX_IDLE);
            poolConfig.setMinIdle(MIN_IDLE);
            poolConfig.setMaxWaitMillis(MAX_WAIT_MILLIS);
            poolConfig.setTestOnBorrow(TEST_ON_BORROW);
            
            if (password != null && !password.isEmpty()) {
                jedisPool = new JedisPool(poolConfig, host, port, timeout, password, database);
            } else {
                jedisPool = new JedisPool(poolConfig, host, port, timeout);
            }
            
            logger.info("Redis连接池初始化成功，连接到 {}:{}", host, port);
        } catch (Exception e) {
            logger.error("Redis连接池初始化失败", e);
        }
    }
    
    /**
     * 获取Jedis实例
     * @return Jedis实例
     */
    public static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                return jedisPool.getResource();
            } else {
                throw new RuntimeException("JedisPool未初始化");
            }
        } catch (Exception e) {
            logger.error("获取Jedis实例失败", e);
            throw new RuntimeException("获取Jedis实例失败", e);
        }
    }
    
    /**
     * 关闭连接池
     */
    public static void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
            logger.info("Redis连接池已关闭");
        }
    }
}