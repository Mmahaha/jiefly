package com.example.zookeeper.election;

import com.example.zookeeper.util.ZKClientUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Leader选举实现
 * 基于Curator的LeaderSelector实现Leader选举
 */
public class LeaderElection extends LeaderSelectorListenerAdapter implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(LeaderElection.class);
    
    // Leader选举路径
    private static final String LEADER_PATH = "/election";
    
    private final String name;
    private final LeaderSelector leaderSelector;
    private final AtomicInteger leaderCount = new AtomicInteger(0);
    
    // Leader工作任务
    private LeaderTask leaderTask;
    
    /**
     * 创建Leader选举实例
     * @param name 当前实例名称
     * @param leaderPath 选举路径
     * @param leaderTask Leader工作任务
     */
    public LeaderElection(String name, String leaderPath, LeaderTask leaderTask) {
        this.name = name;
        this.leaderTask = leaderTask;
        
        CuratorFramework client = ZKClientUtil.getClient();
        String path = LEADER_PATH + "/" + leaderPath;
        
        // 创建Leader选择器
        leaderSelector = new LeaderSelector(client, path, this);
        
        // 确保在此实例释放领导权后还可以再次获得领导权
        leaderSelector.autoRequeue();
        
        logger.info("创建Leader选举实例: {}, 路径: {}", name, path);
    }
    
    /**
     * 启动Leader选举
     */
    public void start() {
        leaderSelector.start();
        logger.info("启动Leader选举: {}", name);
    }
    
    /**
     * 当成为Leader时调用此方法
     */
    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        // 记录成为Leader的次数
        int leaderCount = this.leaderCount.incrementAndGet();
        logger.info("实例 {} 成为Leader，这是第 {} 次成为Leader", name, leaderCount);
        
        try {
            // 执行Leader任务
            if (leaderTask != null) {
                leaderTask.execute();
            }
            
            // 模拟工作，保持Leader状态
            // 在实际应用中，这里应该是Leader的工作循环
            while (true) {
                // 检查是否还是Leader
                if (!leaderSelector.hasLeadership()) {
                    logger.info("实例 {} 已不再是Leader", name);
                    break;
                }
                
                // 执行Leader周期性工作
                if (leaderTask != null) {
                    leaderTask.heartbeat();
                }
                
                // 休眠一段时间
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (InterruptedException e) {
            logger.info("实例 {} 的Leader工作被中断", name);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("实例 " + name + " 执行Leader工作时发生错误", e);
        } finally {
            logger.info("实例 {} 释放Leader权限", name);
        }
    }
    
    /**
     * 检查当前实例是否是Leader
     * @return 是否是Leader
     */
    public boolean isLeader() {
        return leaderSelector.hasLeadership();
    }
    
    /**
     * 关闭Leader选举
     */
    @Override
    public void close() throws IOException {
        leaderSelector.close();
        logger.info("关闭Leader选举: {}", name);
    }
    
    /**
     * Leader任务接口
     */
    public interface LeaderTask {
        /**
         * 成为Leader时执行的初始化任务
         * @throws Exception 执行过程中可能抛出的异常
         */
        void execute() throws Exception;
        
        /**
         * Leader周期性执行的心跳任务
         * @throws Exception 执行过程中可能抛出的异常
         */
        void heartbeat() throws Exception;
    }
}