package com.example.zookeeper.example;

import com.example.zookeeper.config.ConfigCenter;
import com.example.zookeeper.election.LeaderElection;
import com.example.zookeeper.lock.DistributedLock;
import com.example.zookeeper.lock.SharedLock;
import com.example.zookeeper.registry.ServiceDiscovery;
import com.example.zookeeper.registry.ServiceRegistry;
import com.example.zookeeper.util.ZKClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * ZooKeeper功能示例
 * 展示ZooKeeper的各种常见用法
 */
public class ZKExample {
    private static final Logger logger = LoggerFactory.getLogger(ZKExample.class);
    
    public static void main(String[] args) {
        try {
            // 初始化ZooKeeper客户端
            ZKClientUtil.getClient();
            
            // 运行各种示例
//            registryExample();
            lockExample();
//            configExample();
//            leaderElectionExample();
            
            // 等待一段时间，以便观察结果
//            Thread.sleep(60000);
            
            // 关闭ZooKeeper客户端
            ZKClientUtil.closeClient();
        } catch (Exception e) {
            logger.error("示例运行出错", e);
        }
    }
    
    /**
     * 服务注册与发现示例
     */
    private static void registryExample() throws Exception {
        logger.info("===== 服务注册与发现示例 =====");
        
        // 创建服务注册中心
        ServiceRegistry registry = new ServiceRegistry();
        
        // 注册服务
        registry.register("userService", "192.168.1.100:8080");
        registry.register("userService", "192.168.1.101:8080");
        registry.register("orderService", "192.168.1.100:8081");
        
        // 创建服务发现
        ServiceDiscovery discovery = new ServiceDiscovery();
        
        // 发现服务
        discovery.discover("userService");
        discovery.discover("orderService");
        
        // 获取服务地址
        String userServiceAddress = discovery.getServiceAddress("userService");
        String orderServiceAddress = discovery.getServiceAddress("orderService");
        
        logger.info("获取到userService地址: {}", userServiceAddress);
        logger.info("获取到orderService地址: {}", orderServiceAddress);
        
        // 模拟服务下线
        Thread.sleep(2000);
        registry.unregister("userService", "192.168.1.101:8080");
        
        // 再次获取服务地址
        Thread.sleep(1000);
        userServiceAddress = discovery.getServiceAddress("userService");
        logger.info("服务下线后，获取到userService地址: {}", userServiceAddress);
    }
    
    /**
     * 分布式锁示例
     */
    private static void lockExample() throws Exception {
        logger.info("\n===== 分布式锁示例 =====");
        
        // 创建分布式锁
        DistributedLock lock = new DistributedLock("example-lock");
        
        // 使用锁执行任务
        String result = lock.executeWithLock(new DistributedLock.LockTask<String>() {
            @Override
            public String execute() throws Exception {
                logger.info("获取到锁，执行任务中...");
                // 模拟任务执行
                Thread.sleep(1000);
                return "任务执行成功";
            }
        });
        
        logger.info("锁任务执行结果: {}", result);
        
        // 模拟多线程竞争锁
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    DistributedLock threadLock = new DistributedLock("thread-lock");
                    boolean acquired = threadLock.acquire(5, TimeUnit.SECONDS);
                    if (acquired) {
                        logger.info("线程 {} 获取到锁", index);
                        // 模拟工作
                        Thread.sleep(1000);
                        threadLock.release();
                        logger.info("线程 {} 释放了锁", index);
                    } else {
                        logger.info("线程 {} 未能获取到锁", index);
                    }
                } catch (Exception e) {
                    logger.error("线程 " + index + " 执行出错", e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        // 等待所有线程完成
        latch.await();
        
        // 共享锁示例
        logger.info("\n===== 共享锁示例 =====");
        
        // 创建共享锁
        SharedLock sharedLock = new SharedLock("shared-lock-example");
        
        // 模拟多个读操作同时进行
        int readerCount = 3;
        CountDownLatch readLatch = new CountDownLatch(readerCount);
        
        for (int i = 0; i < readerCount; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    // 获取读锁
                    sharedLock.acquireReadLock();
                    logger.info("读线程 {} 获取到读锁", index);
                    // 模拟读操作
                    Thread.sleep(2000);
                    logger.info("读线程 {} 完成读操作", index);
                    sharedLock.releaseReadLock();
                } catch (Exception e) {
                    logger.error("读线程 " + index + " 执行出错", e);
                } finally {
                    readLatch.countDown();
                }
            }).start();
        }
        
        // 等待一段时间，让读线程先启动
        Thread.sleep(500);
        
        // 模拟写操作
        new Thread(() -> {
            try {
                logger.info("写线程尝试获取写锁");
                // 获取写锁
                sharedLock.acquireWriteLock();
                logger.info("写线程获取到写锁，开始写操作");
                // 模拟写操作
                Thread.sleep(1000);
                logger.info("写线程完成写操作，释放写锁");
                sharedLock.releaseWriteLock();
            } catch (Exception e) {
                logger.error("写线程执行出错", e);
            }
        }).start();
        
        // 等待所有读线程完成
        readLatch.await();
        
        // 使用读写锁执行任务示例
        String readResult = sharedLock.executeWithReadLock(new DistributedLock.LockTask<String>() {
            @Override
            public String execute() throws Exception {
                logger.info("使用读锁执行任务...");
                Thread.sleep(500);
                return "读取数据成功";
            }
        });
        logger.info("读锁任务执行结果: {}", readResult);
        
        String writeResult = sharedLock.executeWithWriteLock(new DistributedLock.LockTask<String>() {
            @Override
            public String execute() throws Exception {
                logger.info("使用写锁执行任务...");
                Thread.sleep(500);
                return "写入数据成功";
            }
        });
        logger.info("写锁任务执行结果: {}", writeResult);
    }
    
    /**
     * 配置中心示例
     */
    private static void configExample() throws Exception {
        logger.info("\n===== 配置中心示例 =====");
        
        // 创建配置中心
        ConfigCenter configCenter = new ConfigCenter();
        
        // 设置配置
        configCenter.setConfig("app.name", "ZooKeeper示例应用");
        configCenter.setConfig("app.version", "1.0.0");
        configCenter.setConfig("db.url", "jdbc:mysql://localhost:3306/test");
        
        // 获取配置
        String appName = configCenter.getConfig("app.name");
        String dbUrl = configCenter.getConfig("db.url");
        
        logger.info("获取配置 - app.name: {}", appName);
        logger.info("获取配置 - db.url: {}", dbUrl);
        
        // 监听配置变化
        configCenter.watchConfig("app.version", new ConfigCenter.ConfigChangeListener() {
            @Override
            public void configChanged(String key, String oldValue, String newValue) {
                logger.info("配置变更通知 - {}: {} -> {}", key, oldValue, newValue);
            }
        });
        
        // 更新配置，触发监听器
        Thread.sleep(1000);
        configCenter.setConfig("app.version", "1.0.1");
        
        // 删除配置
        Thread.sleep(1000);
        configCenter.deleteConfig("db.url");
        dbUrl = configCenter.getConfig("db.url");
        logger.info("删除后获取配置 - db.url: {}", dbUrl);
    }
    
    /**
     * Leader选举示例
     */
    private static void leaderElectionExample() throws Exception {
        logger.info("\n===== Leader选举示例 =====");
        
        // 创建多个Leader选举实例
        LeaderElection election1 = new LeaderElection("节点1", "master", new LeaderElection.LeaderTask() {
            @Override
            public void execute() throws Exception {
                logger.info("节点1成为Leader，执行初始化任务");
            }
            
            @Override
            public void heartbeat() throws Exception {
                logger.info("节点1作为Leader，执行心跳任务");
            }
        });
        
        LeaderElection election2 = new LeaderElection("节点2", "master", new LeaderElection.LeaderTask() {
            @Override
            public void execute() throws Exception {
                logger.info("节点2成为Leader，执行初始化任务");
            }
            
            @Override
            public void heartbeat() throws Exception {
                logger.info("节点2作为Leader，执行心跳任务");
            }
        });
        
        // 启动Leader选举
        election1.start();
        election2.start();
        
        // 等待一段时间，让Leader选举稳定
        Thread.sleep(5000);
        
        // 检查Leader状态
        logger.info("节点1是否是Leader: {}", election1.isLeader());
        logger.info("节点2是否是Leader: {}", election2.isLeader());
        
        // 关闭当前Leader，触发重新选举
        if (election1.isLeader()) {
            logger.info("关闭当前Leader: 节点1");
            election1.close();
        } else if (election2.isLeader()) {
            logger.info("关闭当前Leader: 节点2");
            election2.close();
        }
        
        // 等待重新选举完成
        Thread.sleep(5000);
        
        // 再次检查Leader状态
        if (!election1.isLeader() && !election2.isLeader()) {
            logger.info("没有节点成为Leader");
        } else {
            logger.info("节点1是否是Leader: {}", election1.isLeader());
            logger.info("节点2是否是Leader: {}", election2.isLeader());
        }
    }
}