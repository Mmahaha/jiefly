package org.example.redis.lock;

import org.example.redis.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Redis分布式锁实现
 * 基于Redis的SETNX命令实现的分布式锁
 */
public class RedisDistributedLock {
    private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);
    
    // 锁前缀
    private static final String LOCK_PREFIX = "redis:lock:";
    // 默认锁过期时间（秒）
    private static final int DEFAULT_EXPIRE_TIME = 30;
    // 默认获取锁的重试次数
    private static final int DEFAULT_RETRY_TIMES = 3;
    // 默认重试间隔（毫秒）
    private static final long DEFAULT_RETRY_INTERVAL = 100;
    
    private final String lockKey;
    private final String lockValue;
    private final int expireTime;
    
    /**
     * 创建分布式锁
     *
     * @param lockName 锁名称
     */
    public RedisDistributedLock(String lockName) {
        this(lockName, DEFAULT_EXPIRE_TIME);
    }
    
    /**
     * 创建分布式锁
     *
     * @param lockName   锁名称
     * @param expireTime 锁过期时间（秒）
     */
    public RedisDistributedLock(String lockName, int expireTime) {
        this.lockKey = LOCK_PREFIX + lockName;
        // 使用UUID作为锁的值，确保唯一性
        this.lockValue = UUID.randomUUID().toString();
        this.expireTime = expireTime;
    }
    
    /**
     * 尝试获取锁
     *
     * @return 是否成功获取锁
     */
    public boolean tryLock() {
        try {
            boolean result = RedisUtil.setNx(lockKey, lockValue);
            if (result) {
                // 设置过期时间，防止死锁
                RedisUtil.expire(lockKey, expireTime);
                logger.debug("获取锁成功: {}", lockKey);
            }
            return result;
        } catch (Exception e) {
            logger.error("获取锁异常: {}", lockKey, e);
            return false;
        }
    }
    
    /**
     * 尝试获取锁，如果失败则重试
     *
     * @param retryTimes     重试次数
     * @param retryInterval  重试间隔（毫秒）
     * @return 是否成功获取锁
     */
    public boolean tryLockWithRetry(int retryTimes, long retryInterval) {
        for (int i = 0; i <= retryTimes; i++) {
            if (tryLock()) {
                return true;
            }
            
            if (i < retryTimes) {
                try {
                    logger.debug("获取锁失败，{}ms后重试: {}", retryInterval, lockKey);
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        logger.debug("获取锁最终失败: {}", lockKey);
        return false;
    }
    
    /**
     * 尝试获取锁，使用默认的重试策略
     *
     * @return 是否成功获取锁
     */
    public boolean tryLockWithDefaultRetry() {
        return tryLockWithRetry(DEFAULT_RETRY_TIMES, DEFAULT_RETRY_INTERVAL);
    }
    
    /**
     * 释放锁
     * 注意：只有当前持有锁的客户端才能释放锁
     *
     * @return 是否成功释放锁
     */
    public boolean unlock() {
        try {
            // 确保只释放自己的锁
            String currentValue = RedisUtil.get(lockKey);
            if (lockValue.equals(currentValue)) {
                boolean result = RedisUtil.delete(lockKey);
                logger.debug("释放锁{}: {}", result ? "成功" : "失败", lockKey);
                return result;
            } else {
                logger.warn("尝试释放非自己持有的锁: {}", lockKey);
                return false;
            }
        } catch (Exception e) {
            logger.error("释放锁异常: {}", lockKey, e);
            return false;
        }
    }
    
    /**
     * 获取锁并执行任务
     *
     * @param task 要执行的任务
     * @param <T>  任务返回值类型
     * @return 任务执行结果，如果获取锁失败则返回null
     */
    public <T> T executeWithLock(LockTask<T> task) {
        if (tryLock()) {
            try {
                return task.execute();
            } finally {
                unlock();
            }
        }
        return null;
    }
    
    /**
     * 获取锁并执行任务，如果获取锁失败则重试
     *
     * @param task          要执行的任务
     * @param retryTimes    重试次数
     * @param retryInterval 重试间隔（毫秒）
     * @param <T>           任务返回值类型
     * @return 任务执行结果，如果获取锁失败则返回null
     */
    public <T> T executeWithLockRetry(LockTask<T> task, int retryTimes, long retryInterval) {
        if (tryLockWithRetry(retryTimes, retryInterval)) {
            try {
                return task.execute();
            } finally {
                unlock();
            }
        }
        return null;
    }
    
    /**
     * 锁任务接口
     *
     * @param <T> 任务返回值类型
     */
    @FunctionalInterface
    public interface LockTask<T> {
        /**
         * 执行任务
         *
         * @return 任务结果
         */
        T execute();
    }
}