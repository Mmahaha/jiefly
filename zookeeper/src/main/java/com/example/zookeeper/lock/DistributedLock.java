package com.example.zookeeper.lock;

import com.example.zookeeper.util.ZKClientUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁实现
 * 基于Curator的InterProcessMutex实现分布式锁
 */
public class DistributedLock {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);
    
    // 锁的根路径
    private static final String LOCK_PATH = "/locks";
    
    private final CuratorFramework client;
    private final InterProcessMutex lock;
    private final String lockPath;
    
    /**
     * 创建分布式锁
     * @param lockName 锁名称
     */
    public DistributedLock(String lockName) {
        this.client = ZKClientUtil.getClient();
        this.lockPath = LOCK_PATH + "/" + lockName;
        this.lock = new InterProcessMutex(client, lockPath);
        logger.debug("创建分布式锁: {}", lockPath);
    }
    
    /**
     * 获取锁，如果锁不可用，则阻塞等待
     */
    public void acquire() throws Exception {
        lock.acquire();
        logger.debug("获取锁成功: {}", lockPath);
    }
    
    /**
     * 尝试获取锁，在指定时间内
     * @param time 等待时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    public boolean acquire(long time, TimeUnit unit) throws Exception {
        boolean acquired = lock.acquire(time, unit);
        if (acquired) {
            logger.debug("在指定时间内获取锁成功: {}", lockPath);
        } else {
            logger.debug("在指定时间内获取锁失败: {}", lockPath);
        }
        return acquired;
    }
    
    /**
     * 释放锁
     */
    public void release() throws Exception {
        if (lock.isAcquiredInThisProcess()) {
            lock.release();
            logger.debug("释放锁: {}", lockPath);
        }
    }
    
    /**
     * 使用分布式锁执行任务
     * @param task 要执行的任务
     * @param <T> 任务返回类型
     * @return 任务执行结果
     */
    public <T> T executeWithLock(LockTask<T> task) throws Exception {
        try {
            acquire();
            return task.execute();
        } finally {
            release();
        }
    }
    
    /**
     * 使用分布式锁执行任务，带超时时间
     * @param task 要执行的任务
     * @param time 等待锁的时间
     * @param unit 时间单位
     * @param <T> 任务返回类型
     * @return 任务执行结果，如果获取锁失败则返回null
     */
    public <T> T executeWithLock(LockTask<T> task, long time, TimeUnit unit) throws Exception {
        if (acquire(time, unit)) {
            try {
                return task.execute();
            } finally {
                release();
            }
        }
        return null;
    }
    
    /**
     * 锁任务接口
     * @param <T> 任务返回类型
     */
    public interface LockTask<T> {
        /**
         * 执行任务
         * @return 任务结果
         * @throws Exception 执行过程中可能抛出的异常
         */
        T execute() throws Exception;
    }
}