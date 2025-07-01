package com.example.zookeeper.lock;

import com.example.zookeeper.util.ZKClientUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 共享锁实现
 * 基于Curator的InterProcessReadWriteLock实现读写锁
 * 读锁是共享的，可以同时被多个客户端获取
 * 写锁是排他的，同一时间只能被一个客户端获取
 */
public class SharedLock {
    private static final Logger logger = LoggerFactory.getLogger(SharedLock.class);
    
    // 锁的根路径
    private static final String LOCK_PATH = "/rwlocks";
    
    private final CuratorFramework client;
    private final InterProcessReadWriteLock lock;
    private final String lockPath;
    
    /**
     * 创建读写锁
     * @param lockName 锁名称
     */
    public SharedLock(String lockName) {
        this.client = ZKClientUtil.getClient();
        this.lockPath = LOCK_PATH + "/" + lockName;
        this.lock = new InterProcessReadWriteLock(client, lockPath);
        logger.debug("创建读写锁: {}", lockPath);
    }
    
    /**
     * 获取读锁（共享锁），如果锁不可用，则阻塞等待
     */
    public void acquireReadLock() throws Exception {
        lock.readLock().acquire();
        logger.debug("获取读锁成功: {}", lockPath);
    }
    
    /**
     * 尝试获取读锁，在指定时间内
     * @param time 等待时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    public boolean acquireReadLock(long time, TimeUnit unit) throws Exception {
        boolean acquired = lock.readLock().acquire(time, unit);
        if (acquired) {
            logger.debug("在指定时间内获取读锁成功: {}", lockPath);
        } else {
            logger.debug("在指定时间内获取读锁失败: {}", lockPath);
        }
        return acquired;
    }
    
    /**
     * 释放读锁
     */
    public void releaseReadLock() throws Exception {
        if (lock.readLock().isAcquiredInThisProcess()) {
            lock.readLock().release();
            logger.debug("释放读锁: {}", lockPath);
        }
    }
    
    /**
     * 获取写锁（排他锁），如果锁不可用，则阻塞等待
     */
    public void acquireWriteLock() throws Exception {
        lock.writeLock().acquire();
        logger.debug("获取写锁成功: {}", lockPath);
    }
    
    /**
     * 尝试获取写锁，在指定时间内
     * @param time 等待时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    public boolean acquireWriteLock(long time, TimeUnit unit) throws Exception {
        boolean acquired = lock.writeLock().acquire(time, unit);
        if (acquired) {
            logger.debug("在指定时间内获取写锁成功: {}", lockPath);
        } else {
            logger.debug("在指定时间内获取写锁失败: {}", lockPath);
        }
        return acquired;
    }
    
    /**
     * 释放写锁
     */
    public void releaseWriteLock() throws Exception {
        if (lock.writeLock().isAcquiredInThisProcess()) {
            lock.writeLock().release();
            logger.debug("释放写锁: {}", lockPath);
        }
    }
    
    /**
     * 使用读锁执行任务
     * @param task 要执行的任务
     * @param <T> 任务返回类型
     * @return 任务执行结果
     */
    public <T> T executeWithReadLock(DistributedLock.LockTask<T> task) throws Exception {
        try {
            acquireReadLock();
            return task.execute();
        } finally {
            releaseReadLock();
        }
    }
    
    /**
     * 使用读锁执行任务，带超时时间
     * @param task 要执行的任务
     * @param time 等待锁的时间
     * @param unit 时间单位
     * @param <T> 任务返回类型
     * @return 任务执行结果，如果获取锁失败则返回null
     */
    public <T> T executeWithReadLock(DistributedLock.LockTask<T> task, long time, TimeUnit unit) throws Exception {
        if (acquireReadLock(time, unit)) {
            try {
                return task.execute();
            } finally {
                releaseReadLock();
            }
        }
        return null;
    }
    
    /**
     * 使用写锁执行任务
     * @param task 要执行的任务
     * @param <T> 任务返回类型
     * @return 任务执行结果
     */
    public <T> T executeWithWriteLock(DistributedLock.LockTask<T> task) throws Exception {
        try {
            acquireWriteLock();
            return task.execute();
        } finally {
            releaseWriteLock();
        }
    }
    
    /**
     * 使用写锁执行任务，带超时时间
     * @param task 要执行的任务
     * @param time 等待锁的时间
     * @param unit 时间单位
     * @param <T> 任务返回类型
     * @return 任务执行结果，如果获取锁失败则返回null
     */
    public <T> T executeWithWriteLock(DistributedLock.LockTask<T> task, long time, TimeUnit unit) throws Exception {
        if (acquireWriteLock(time, unit)) {
            try {
                return task.execute();
            } finally {
                releaseWriteLock();
            }
        }
        return null;
    }
}