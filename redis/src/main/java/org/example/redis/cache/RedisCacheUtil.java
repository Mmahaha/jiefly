package org.example.redis.cache;

import org.example.redis.lock.RedisDistributedLock;
import org.example.redis.util.RedisJsonUtil;
import org.example.redis.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Redis缓存工具类
 * 实现常见的缓存模式和缓存问题解决方案
 */
public class RedisCacheUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheUtil.class);
    
    // 空值缓存的过期时间（秒），用于解决缓存穿透
    private static final int NULL_VALUE_EXPIRE = 60;
    // 默认缓存过期时间（秒）
    private static final int DEFAULT_EXPIRE = 3600;
    // 缓存过期时间随机范围（秒），用于解决缓存雪崩
    private static final int EXPIRE_RANDOM_RANGE = 300;
    
    /**
     * 实现Cache-Aside模式（旁路缓存模式）
     * 1. 先查询缓存，命中则返回
     * 2. 缓存未命中，则查询数据库
     * 3. 将数据库查询结果写入缓存，并返回结果
     *
     * @param key      缓存键
     * @param dbFallback 数据库查询逻辑
     * @param expireSeconds 过期时间（秒）
     * @param <T>      返回数据类型
     * @return 查询结果
     */
    public static <T> T getWithCacheAside(String key, Class<T> clazz, Supplier<T> dbFallback, int expireSeconds) {
        // 1. 查询缓存
        T cacheResult = RedisJsonUtil.getJson(key, clazz);
        if (cacheResult != null) {
            logger.debug("缓存命中: {}", key);
            return cacheResult;
        }
        
        logger.debug("缓存未命中: {}", key);
        
        // 2. 查询数据库
        T dbResult = dbFallback.get();
        
        // 3. 写入缓存
        if (dbResult != null) {
            // 添加随机过期时间，避免缓存雪崩
            int finalExpire = expireSeconds + ThreadLocalRandom.current().nextInt(EXPIRE_RANDOM_RANGE);
            RedisJsonUtil.setJsonEx(key, dbResult, finalExpire);
            logger.debug("数据写入缓存: {}, 过期时间: {}秒", key, finalExpire);
        } else {
            // 缓存空值，避免缓存穿透
            RedisUtil.setEx(key, "", NULL_VALUE_EXPIRE);
            logger.debug("空值写入缓存: {}, 过期时间: {}秒", key, NULL_VALUE_EXPIRE);
        }
        
        return dbResult;
    }
    
    /**
     * 使用默认过期时间的Cache-Aside模式
     */
    public static <T> T getWithCacheAside(String key, Class<T> clazz, Supplier<T> dbFallback) {
        return getWithCacheAside(key, clazz, dbFallback, DEFAULT_EXPIRE);
    }
    
    /**
     * 解决缓存击穿问题的Cache-Aside模式（使用分布式锁）
     * 缓存击穿：热点数据过期时，大量请求同时查询数据库
     *
     * @param key      缓存键
     * @param dbFallback 数据库查询逻辑
     * @param expireSeconds 过期时间（秒）
     * @param <T>      返回数据类型
     * @return 查询结果
     */
    public static <T> T getWithLock(String key, Class<T> clazz, Supplier<T> dbFallback, int expireSeconds) {
        // 1. 查询缓存
        T cacheResult = RedisJsonUtil.getJson(key, clazz);
        if (cacheResult != null) {
            logger.debug("缓存命中: {}", key);
            return cacheResult;
        }
        
        logger.debug("缓存未命中: {}", key);
        
        // 2. 获取分布式锁
        String lockKey = "lock:cache:" + key;
        RedisDistributedLock lock = new RedisDistributedLock(lockKey, 10);
        
        if (lock.tryLock()) {
            try {
                // 双重检查，避免其他线程已经更新了缓存
                cacheResult = RedisJsonUtil.getJson(key, clazz);
                if (cacheResult != null) {
                    logger.debug("获取锁后再次检查，缓存已更新: {}", key);
                    return cacheResult;
                }
                
                // 3. 查询数据库
                T dbResult = dbFallback.get();
                
                // 4. 写入缓存
                if (dbResult != null) {
                    // 添加随机过期时间，避免缓存雪崩
                    int finalExpire = expireSeconds + ThreadLocalRandom.current().nextInt(EXPIRE_RANDOM_RANGE);
                    RedisJsonUtil.setJsonEx(key, dbResult, finalExpire);
                    logger.debug("数据写入缓存: {}, 过期时间: {}秒", key, finalExpire);
                } else {
                    // 缓存空值，避免缓存穿透
                    RedisUtil.setEx(key, "", NULL_VALUE_EXPIRE);
                    logger.debug("空值写入缓存: {}, 过期时间: {}秒", key, NULL_VALUE_EXPIRE);
                }
                
                return dbResult;
            } finally {
                // 释放锁
                lock.unlock();
            }
        } else {
            // 获取锁失败，等待一段时间后重试获取缓存
            try {
                logger.debug("获取锁失败，等待后重试: {}", key);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // 重新查询缓存
            cacheResult = RedisJsonUtil.getJson(key, clazz);
            if (cacheResult != null) {
                logger.debug("重试后缓存命中: {}", key);
                return cacheResult;
            }
            
            // 如果仍然未命中，则直接查询数据库
            logger.debug("重试后缓存仍未命中，直接查询数据库: {}", key);
            return dbFallback.get();
        }
    }
    
    /**
     * 使用默认过期时间的带锁Cache-Aside模式
     */
    public static <T> T getWithLock(String key, Class<T> clazz, Supplier<T> dbFallback) {
        return getWithLock(key, clazz, dbFallback, DEFAULT_EXPIRE);
    }
    
    /**
     * 使用布隆过滤器防止缓存穿透的Cache-Aside模式
     * 注意：此方法需要预先设置布隆过滤器
     *
     * @param key      缓存键
     * @param bloomFilterKey 布隆过滤器键
     * @param dbFallback 数据库查询逻辑
     * @param expireSeconds 过期时间（秒）
     * @param <T>      返回数据类型
     * @return 查询结果
     */
    public static <T> T getWithBloomFilter(String key, String bloomFilterKey, Class<T> clazz, Supplier<T> dbFallback, int expireSeconds) {
        // 1. 检查布隆过滤器
        boolean mayExist = checkBloomFilter(bloomFilterKey, key);
        if (!mayExist) {
            logger.debug("布隆过滤器拦截: {}", key);
            return null;
        }
        
        // 2. 查询缓存
        T cacheResult = RedisJsonUtil.getJson(key, clazz);
        if (cacheResult != null) {
            logger.debug("缓存命中: {}", key);
            return cacheResult;
        }
        
        logger.debug("缓存未命中: {}", key);
        
        // 3. 查询数据库
        T dbResult = dbFallback.get();
        
        // 4. 写入缓存
        if (dbResult != null) {
            // 添加随机过期时间，避免缓存雪崩
            int finalExpire = expireSeconds + ThreadLocalRandom.current().nextInt(EXPIRE_RANDOM_RANGE);
            RedisJsonUtil.setJsonEx(key, dbResult, finalExpire);
            logger.debug("数据写入缓存: {}, 过期时间: {}秒", key, finalExpire);
            
            // 将key添加到布隆过滤器
            addToBloomFilter(bloomFilterKey, key);
        } else {
            // 缓存空值，避免缓存穿透
            RedisUtil.setEx(key, "", NULL_VALUE_EXPIRE);
            logger.debug("空值写入缓存: {}, 过期时间: {}秒", key, NULL_VALUE_EXPIRE);
        }
        
        return dbResult;
    }
    
    /**
     * 使用默认过期时间的带布隆过滤器Cache-Aside模式
     */
    public static <T> T getWithBloomFilter(String key, String bloomFilterKey, Class<T> clazz, Supplier<T> dbFallback) {
        return getWithBloomFilter(key, bloomFilterKey, clazz, dbFallback, DEFAULT_EXPIRE);
    }
    
    /**
     * 检查布隆过滤器中是否可能存在某个值
     * 注意：这里使用Redis的位图操作简单模拟布隆过滤器，实际项目中可以使用专门的布隆过滤器实现
     */
    private static boolean checkBloomFilter(String bloomFilterKey, String value) {
        try {
            // 计算哈希值（这里使用简化的哈希算法，实际应使用多个哈希函数）
            int hash1 = Math.abs(value.hashCode() % 1000);
            int hash2 = Math.abs((value.hashCode() * 16777619) % 1000);
            
            // 检查位图中的位
            return RedisUtil.execute(jedis -> {
                boolean bit1 = jedis.getbit(bloomFilterKey, hash1);
                boolean bit2 = jedis.getbit(bloomFilterKey, hash2);
                return bit1 && bit2;
            });
        } catch (Exception e) {
            logger.error("检查布隆过滤器异常", e);
            // 发生异常时默认返回true，让请求继续处理
            return true;
        }
    }
    
    /**
     * 将值添加到布隆过滤器
     */
    private static void addToBloomFilter(String bloomFilterKey, String value) {
        try {
            // 计算哈希值
            int hash1 = Math.abs(value.hashCode() % 1000);
            int hash2 = Math.abs((value.hashCode() * 16777619) % 1000);
            
            // 设置位图中的位
            RedisUtil.execute(jedis -> {
                jedis.setbit(bloomFilterKey, hash1, true);
                jedis.setbit(bloomFilterKey, hash2, true);
                // 设置布隆过滤器的过期时间（可选）
                jedis.expire(bloomFilterKey, 86400 * 30); // 30天
                return null;
            });
        } catch (Exception e) {
            logger.error("添加到布隆过滤器异常", e);
        }
    }
    
    /**
     * 删除缓存
     */
    public static boolean deleteCache(String key) {
        return RedisUtil.delete(key);
    }
    
    /**
     * 更新缓存（先更新数据库，再删除缓存）
     * 这是常见的缓存更新策略，避免缓存与数据库不一致
     *
     * @param key      缓存键
     * @param dbUpdate 数据库更新逻辑
     * @return 是否成功
     */
    public static boolean updateCache(String key, Runnable dbUpdate) {
        try {
            // 1. 更新数据库
            dbUpdate.run();
            
            // 2. 删除缓存
            boolean deleted = RedisUtil.delete(key);
            logger.debug("缓存删除{}: {}", deleted ? "成功" : "失败", key);
            
            return true;
        } catch (Exception e) {
            logger.error("更新缓存异常: {}", key, e);
            return false;
        }
    }
}