package org.example.redis.example;

import org.example.redis.cache.RedisCacheUtil;
import org.example.redis.config.RedisConfig;
import org.example.redis.counter.RedisCounter;
import org.example.redis.geo.RedisGeoUtil;
import org.example.redis.leaderboard.RedisLeaderboard;
import org.example.redis.lock.RedisDistributedLock;
import org.example.redis.mq.RedisMessageQueue;
import org.example.redis.ratelimit.RedisRateLimiter;
import org.example.redis.util.RedisJsonUtil;
import org.example.redis.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.args.GeoUnit;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Redis示例类
 * 展示Redis在实际工作场景中的常见用法
 */
public class RedisExamples {
    private static final Logger logger = LoggerFactory.getLogger(RedisExamples.class);
    
    /**
     * 运行所有示例
     */
    public static void main(String[] args) {
        try {
            // 初始化Redis连接池
//            RedisConfig.initJedisPool("localhost", 6379, null, 0, 2000);
            
            // 运行基本操作示例
            basicOperationsExample();
            
            // 运行JSON操作示例
            jsonOperationsExample();
            
            // 运行分布式锁示例
            distributedLockExample();
            
            // 运行限流器示例
            rateLimiterExample();
            
            // 运行缓存示例
            cacheExample();
            
            // 运行消息队列示例
            messageQueueExample();
            
            // 运行计数器示例
            counterExample();
            
            // 运行地理位置示例
            geoExample();
            
            // 运行排行榜示例
            leaderboardExample();
            
            logger.info("所有示例运行完成");
        } catch (Exception e) {
            logger.error("运行示例时发生异常", e);
        } finally {
            // 关闭Redis连接池
            RedisConfig.close();
        }
    }
    
    /**
     * 基本操作示例
     */
    public static void basicOperationsExample() {
        logger.info("===== 运行基本操作示例 =====");
        
        // 字符串操作
        RedisUtil.set("user:1:name", "张三");
        RedisUtil.setEx("user:1:token", "TOKEN123456", 3600); // 1小时过期
        
        String name = RedisUtil.get("user:1:name");
        logger.info("获取用户名: {}", name);
        
        // 计数器
        RedisUtil.set("product:1:views", "0");
        long views = RedisUtil.incr("product:1:views");
        logger.info("产品浏览次数: {}", views);
        
        // 哈希表操作
        RedisUtil.hSet("user:1", "name", "张三");
        RedisUtil.hSet("user:1", "email", "zhangsan@example.com");
        RedisUtil.hSet("user:1", "age", "30");
        
        Map<String, String> userMap = RedisUtil.hGetAll("user:1");
        logger.info("用户信息: {}", userMap);
        
        // 列表操作
        RedisUtil.lPush("user:1:notifications", "通知1", "通知2", "通知3");
        List<String> notifications = RedisUtil.lRange("user:1:notifications", 0, -1);
        logger.info("用户通知: {}", notifications);
        
        // 集合操作
        RedisUtil.sAdd("user:1:roles", "user", "editor");
        RedisUtil.sAdd("user:2:roles", "user", "admin");
        
        Set<String> roles = RedisUtil.sMembers("user:1:roles");
        logger.info("用户角色: {}", roles);
        
        Set<String> commonRoles = RedisUtil.sInter("user:1:roles", "user:2:roles");
        logger.info("共同角色: {}", commonRoles);
        
        // 有序集合操作
        RedisUtil.zAdd("leaderboard", 100, "user:1");
        RedisUtil.zAdd("leaderboard", 200, "user:2");
        RedisUtil.zAdd("leaderboard", 150, "user:3");
        
        List<String> topUsers = RedisUtil.zRevRange("leaderboard", 0, 2);
        logger.info("排行榜前三名: {}", topUsers);
        
        // 清理示例数据
        RedisUtil.delete(
                "user:1:name", "user:1:token", "product:1:views",
                "user:1", "user:1:notifications", "user:1:roles",
                "user:2:roles", "leaderboard"
        );
    }
    
    /**
     * JSON操作示例
     */
    public static void jsonOperationsExample() {
        logger.info("===== 运行JSON操作示例 =====");
        
        // 创建用户对象
        User user = new User(1, "张三", "zhangsan@example.com", 30);
        
        // 存储用户对象
        RedisJsonUtil.setJson("user:1:json", user);
        
        // 获取用户对象
        User retrievedUser = RedisJsonUtil.getJson("user:1:json", User.class);
        logger.info("获取的用户: {}", retrievedUser);
        
        // 存储用户列表
        List<User> users = new ArrayList<>();
        users.add(new User(1, "张三", "zhangsan@example.com", 30));
        users.add(new User(2, "李四", "lisi@example.com", 25));
        users.add(new User(3, "王五", "wangwu@example.com", 35));
        
        RedisJsonUtil.setJson("users:list", users);
        
        // 获取用户列表
        List<User> retrievedUsers = RedisJsonUtil.getJson("users:list", new com.fasterxml.jackson.core.type.TypeReference<List<User>>() {});
        logger.info("获取的用户列表: {}", retrievedUsers);
        
        // 在哈希表中存储JSON
        Map<String, User> userMap = new HashMap<>();
        userMap.put("user:1", user);
        userMap.put("user:2", new User(2, "李四", "lisi@example.com", 25));
        
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            RedisJsonUtil.hSetJson("users:map", entry.getKey(), entry.getValue());
        }
        
        // 从哈希表获取JSON
        User user1 = RedisJsonUtil.hGetJson("users:map", "user:1", User.class);
        logger.info("从哈希表获取的用户: {}", user1);
        
        // 清理示例数据
        RedisUtil.delete("user:1:json", "users:list", "users:map");
    }
    
    /**
     * 分布式锁示例
     */
    public static void distributedLockExample() {
        logger.info("===== 运行分布式锁示例 =====");
        
        // 创建分布式锁
        RedisDistributedLock lock = new RedisDistributedLock("order:create");
        
        // 模拟多线程环境下的并发操作
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);
        
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    // 尝试获取锁
                    if (lock.tryLockWithDefaultRetry()) {
                        try {
                            logger.info("线程 {} 获取到锁，执行业务逻辑", threadId);
                            // 模拟业务操作
                            Thread.sleep(100);
                        } finally {
                            // 释放锁
                            lock.unlock();
                            logger.info("线程 {} 释放锁", threadId);
                        }
                    } else {
                        logger.info("线程 {} 未能获取到锁", threadId);
                    }
                } catch (Exception e) {
                    logger.error("线程 {} 执行异常", threadId, e);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        executor.shutdown();
        
        // 使用锁执行任务
        String result = lock.executeWithLock(() -> {
            logger.info("在锁保护下执行任务");
            return "任务执行成功";
        });
        
        logger.info("任务执行结果: {}", result);
    }
    
    /**
     * 限流器示例
     */
    public static void rateLimiterExample() {
        logger.info("===== 运行限流器示例 =====");
        
        // 创建固定窗口限流器（每秒最多5个请求）
        RedisRateLimiter.FixedWindow fixedWindowLimiter = new RedisRateLimiter.FixedWindow("api:user", 5, 1);
        
        // 创建滑动窗口限流器（每5秒最多10个请求）
        RedisRateLimiter.SlidingWindow slidingWindowLimiter = new RedisRateLimiter.SlidingWindow("api:order", 10, 5);
        
        // 创建令牌桶限流器（容量为10，每秒填充2个令牌）
        RedisRateLimiter.TokenBucket tokenBucketLimiter = new RedisRateLimiter.TokenBucket("api:product", 10, 2);
        
        // 模拟请求
        for (int i = 0; i < 8; i++) {
            boolean allowed1 = fixedWindowLimiter.tryAcquire();
            boolean allowed2 = slidingWindowLimiter.tryAcquire();
            boolean allowed3 = tokenBucketLimiter.tryAcquire();
            
            logger.info("请求 {}: 固定窗口={}, 滑动窗口={}, 令牌桶={}", i + 1, allowed1, allowed2, allowed3);
            
            try {
                Thread.sleep(200); // 每200毫秒发送一个请求
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * 缓存示例
     */
    public static void cacheExample() {
        logger.info("===== 运行缓存示例 =====");
        
        // 模拟数据库查询
        User user = RedisCacheUtil.getWithCacheAside(
                "cache:user:1",
                User.class,
                () -> {
                    logger.info("从数据库查询用户数据");
                    // 模拟数据库查询延迟
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return new User(1, "张三", "zhangsan@example.com", 30);
                },
                60 // 缓存60秒
        );
        
        logger.info("第一次获取用户: {}", user);
        
        // 再次获取，应该从缓存中获取
        user = RedisCacheUtil.getWithCacheAside(
                "cache:user:1",
                User.class,
                () -> {
                    logger.info("从数据库查询用户数据");
                    return new User(1, "张三", "zhangsan@example.com", 30);
                },
                60
        );
        
        logger.info("第二次获取用户: {}", user);
        
        // 使用分布式锁防止缓存击穿
        user = RedisCacheUtil.getWithLock(
                "cache:user:2",
                User.class,
                () -> {
                    logger.info("从数据库查询用户数据（带锁）");
                    return new User(2, "李四", "lisi@example.com", 25);
                },
                60
        );
        
        logger.info("使用分布式锁获取用户: {}", user);
        
        // 更新缓存（先更新数据库，再删除缓存）
        boolean updated = RedisCacheUtil.updateCache("cache:user:1", () -> {
            logger.info("更新数据库中的用户数据");
            // 模拟数据库更新操作
        });
        
        logger.info("更新缓存: {}", updated);
        
        // 清理示例数据
        RedisUtil.delete("cache:user:1", "cache:user:2");
    }
    
    /**
     * 消息队列示例
     */
    public static void messageQueueExample() {
        logger.info("===== 运行消息队列示例 =====");
        
        // 创建基于List的消息队列
        RedisMessageQueue.ListQueue listQueue = new RedisMessageQueue.ListQueue("orders");
        
        // 发送消息
        listQueue.send("订单1");
        listQueue.send("订单2");
        listQueue.send("订单3");
        
        // 发送对象消息
        Order order = new Order(1001, "用户A", 199.99, "已支付");
        listQueue.sendObject(order);
        
        logger.info("队列长度: {}", listQueue.size());
        
        // 接收普通消息（连续接收三个普通消息）
        String message1 = listQueue.receive(1);
        logger.info("接收到消息: {}", message1);
        
        String message2 = listQueue.receive(1);
        logger.info("接收到消息: {}", message2);
        
        String message3 = listQueue.receive(1);
        logger.info("接收到消息: {}", message3);
        
        // 接收对象消息
        Order receivedOrder = listQueue.receiveObject(1, Order.class);
        logger.info("接收到订单: {}", receivedOrder);
        
        // 创建消息处理器
        RedisMessageQueue.ListQueue.MessageConsumer consumer = listQueue.startConsumer(
                message -> logger.info("处理消息: {}", message),
                2 // 使用2个线程处理消息
        );
        
        // 等待消息处理完成
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 停止消费者
        consumer.stop();
        
        // 创建基于Pub/Sub的消息队列
        RedisMessageQueue.PubSubQueue pubSubQueue = new RedisMessageQueue.PubSubQueue("notifications");
        
        // 创建订阅者
        RedisMessageQueue.PubSubQueue.Subscription subscription = pubSubQueue.subscribe(
                (channel, message) -> logger.info("收到通知: {} (频道: {})", message, channel)
        );
        
        // 发布消息
        pubSubQueue.publish("系统通知：服务器将于今晚23:00进行维护");
        pubSubQueue.publish("紧急通知：新功能已上线");
        
        // 等待消息处理完成
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 取消订阅
        subscription.stop();
    }
    
    /**
     * 计数器示例
     */
    public static void counterExample() {
        logger.info("===== 运行计数器示例 =====");
        
        // 创建简单计数器
        RedisCounter.SimpleCounter pageViewCounter = new RedisCounter.SimpleCounter("page:home:views");
        
        // 增加计数
        for (int i = 0; i < 5; i++) {
            long count = pageViewCounter.increment();
            logger.info("页面浏览次数: {}", count);
        }
        
        // 创建日期计数器
        RedisCounter.DateCounter dailyVisitCounter = new RedisCounter.DateCounter("site:visits");
        
        // 增加今天的计数
        long todayVisits = dailyVisitCounter.incrementToday();
        logger.info("今日访问次数: {}", todayVisits);
        
        // 增加指定日期的计数
        LocalDate yesterday = LocalDate.now().minusDays(1);
        dailyVisitCounter.increment(yesterday, 10);
        logger.info("昨日访问次数: {}", dailyVisitCounter.get(yesterday));
        
        // 获取日期范围的计数总和
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();
        long totalVisits = dailyVisitCounter.getRange(startDate, endDate);
        logger.info("两天访问总次数: {}", totalVisits);
        
        // 创建滑动窗口计数器
        RedisCounter.SlidingWindowCounter apiRequestCounter = new RedisCounter.SlidingWindowCounter("api:requests", 60);
        
        // 增加计数
        for (int i = 0; i < 3; i++) {
            long count = apiRequestCounter.increment();
            logger.info("API请求次数（滑动窗口）: {}", count);
        }
        
        // 创建唯一访客计数器
        RedisCounter.UniqueVisitorCounter uvCounter = new RedisCounter.UniqueVisitorCounter("site:uv");
        
        // 添加访客
        uvCounter.add("user1");
        uvCounter.add("user2");
        uvCounter.add("user1"); // 重复访客
        uvCounter.add("user3");
        
        logger.info("唯一访客数: {}", uvCounter.count());
        
        // 清理示例数据
        RedisUtil.delete(
                "redis:counter:page:home:views",
                "redis:counter:date:site:visits:" + LocalDate.now(),
                "redis:counter:date:site:visits:" + yesterday,
                "redis:counter:sliding:api:requests",
                "redis:counter:uv:site:uv"
        );
    }
    
    /**
     * 地理位置示例
     */
    public static void geoExample() {
        logger.info("===== 运行地理位置示例 =====");
        
        // 创建地理位置管理器
        RedisGeoUtil.GeoManager geoManager = new RedisGeoUtil.GeoManager("stores");
        
        // 添加位置
        geoManager.add(new RedisGeoUtil.GeoLocation("store:1", 116.397128, 39.916527, "北京店"));
        geoManager.add(new RedisGeoUtil.GeoLocation("store:2", 121.473701, 31.230416, "上海店"));
        geoManager.add(new RedisGeoUtil.GeoLocation("store:3", 114.057868, 22.543099, "深圳店"));
        geoManager.add(new RedisGeoUtil.GeoLocation("store:4", 113.264385, 23.129112, "广州店"));
        
        // 获取位置信息
        RedisGeoUtil.GeoLocation location = geoManager.getLocation("store:1");
        logger.info("位置信息: {}", location);
        
        // 计算两个位置之间的距离
        Double distance = geoManager.getDistance("store:1", "store:2", GeoUnit.KM);
        logger.info("北京店到上海店的距离: {}公里", distance);
        
        // 查找附近的位置
        List<RedisGeoUtil.GeoLocation> nearbyStores = geoManager.findNearby(114.057868, 22.543099, 300, GeoUnit.KM, 3);
        logger.info("深圳店300公里内的门店: {}", nearbyStores);
        
        // 查找指定位置附近的其他位置
        List<RedisGeoUtil.GeoLocation> nearbyStoresById = geoManager.findNearbyById("store:3", 300, GeoUnit.KM, 3);
        logger.info("深圳店300公里内的其他门店: {}", nearbyStoresById);
        
        // 清理示例数据
        RedisUtil.delete("redis:geo:stores", "redis:geo:info:stores");
    }
    
    /**
     * 排行榜示例
     */
    public static void leaderboardExample() {
        logger.info("===== 运行排行榜示例 =====");
        
        // 创建排行榜（按分数从高到低排序）
        RedisLeaderboard gameLeaderboard = new RedisLeaderboard("game:scores");
        
        // 添加分数
        gameLeaderboard.addOrUpdate("player:1", 100);
        gameLeaderboard.addOrUpdate("player:2", 200);
        gameLeaderboard.addOrUpdate("player:3", 150);
        gameLeaderboard.addOrUpdate("player:4", 120);
        gameLeaderboard.addOrUpdate("player:5", 180);
        
        // 增加分数
        Double newScore = gameLeaderboard.incrementScore("player:1", 50);
        logger.info("玩家1新分数: {}", newScore);
        
        // 获取排行榜前3名
        List<RedisLeaderboard.LeaderboardEntry> top3 = gameLeaderboard.getTop(3);
        logger.info("排行榜前3名: {}", top3);
        
        // 获取玩家排名
        Long rank = gameLeaderboard.getRank("player:1");
        logger.info("玩家1排名: {}", rank != null ? rank + 1 : "未上榜"); // 排名从0开始，显示时+1
        
        // 获取玩家附近的排名
        List<RedisLeaderboard.LeaderboardEntry> aroundPlayer = gameLeaderboard.getAroundMember("player:1", 3);
        logger.info("玩家1附近的排名: {}", aroundPlayer);
        
        // 创建按分数从低到高排序的排行榜（如竞赛用时）
        RedisLeaderboard raceLeaderboard = new RedisLeaderboard("race:times", false);
        
        // 添加用时（秒）
        raceLeaderboard.addOrUpdate("racer:1", 360); // 6分钟
        raceLeaderboard.addOrUpdate("racer:2", 300); // 5分钟
        raceLeaderboard.addOrUpdate("racer:3", 330); // 5分30秒
        
        // 获取排行榜（用时最短的在前）
        List<RedisLeaderboard.LeaderboardEntry> raceTop3 = raceLeaderboard.getTop(3);
        logger.info("竞赛排行榜: {}", raceTop3);
        
        // 清理示例数据
        RedisUtil.delete("redis:leaderboard:game:scores", "redis:leaderboard:race:times");
    }
    
    /**
     * 用户类
     */
    public static class User {
        private int id;
        private String name;
        private String email;
        private int age;
        
        public User() {
        }
        
        public User(int id, String name, String email, int age) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.age = age;
        }
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public int getAge() {
            return age;
        }
        
        public void setAge(int age) {
            this.age = age;
        }
        
        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
    
    /**
     * 订单类
     */
    public static class Order {
        private int id;
        private String customer;
        private double amount;
        private String status;
        
        public Order() {
        }
        
        public Order(int id, String customer, double amount, String status) {
            this.id = id;
            this.customer = customer;
            this.amount = amount;
            this.status = status;
        }
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getCustomer() {
            return customer;
        }
        
        public void setCustomer(String customer) {
            this.customer = customer;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public void setAmount(double amount) {
            this.amount = amount;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        @Override
        public String toString() {
            return "Order{" +
                    "id=" + id +
                    ", customer='" + customer + '\'' +
                    ", amount=" + amount +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
}