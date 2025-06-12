package org.example.redis.counter;

import org.example.redis.config.RedisConfig;
import org.example.redis.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Redis计数器
 * 提供普通计数器和滑动窗口计数器两种实现
 */
public class RedisCounter {
    private static final Logger logger = LoggerFactory.getLogger(RedisCounter.class);
    
    /**
     * 普通计数器
     * 适用于简单的计数场景，如PV、UV统计等
     */
    public static class SimpleCounter {
        private final String counterKey;
        
        /**
         * 创建计数器
         *
         * @param name 计数器名称
         */
        public SimpleCounter(String name) {
            this.counterKey = "redis:counter:" + name;
        }
        
        /**
         * 创建带日期的计数器
         *
         * @param name 计数器名称
         * @param date 日期
         */
        public SimpleCounter(String name, LocalDate date) {
            this.counterKey = "redis:counter:" + name + ":" + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        
        /**
         * 增加计数
         *
         * @return 增加后的值
         */
        public long increment() {
            return increment(1);
        }
        
        /**
         * 增加指定值
         *
         * @param value 增加的值
         * @return 增加后的值
         */
        public long increment(long value) {
            try {
                return RedisUtil.incrBy(counterKey, value);
            } catch (Exception e) {
                logger.error("增加计数异常: {}", counterKey, e);
                return -1;
            }
        }
        
        /**
         * 减少计数
         *
         * @return 减少后的值
         */
        public long decrement() {
            return decrement(1);
        }
        
        /**
         * 减少指定值
         *
         * @param value 减少的值
         * @return 减少后的值
         */
        public long decrement(long value) {
            try {
                return RedisUtil.decrBy(counterKey, value);
            } catch (Exception e) {
                logger.error("减少计数异常: {}", counterKey, e);
                return -1;
            }
        }
        
        /**
         * 获取当前计数
         *
         * @return 当前计数值
         */
        public long get() {
            try {
                String value = RedisUtil.get(counterKey);
                return value == null ? 0 : Long.parseLong(value);
            } catch (Exception e) {
                logger.error("获取计数异常: {}", counterKey, e);
                return 0;
            }
        }
        
        /**
         * 重置计数器
         *
         * @return 是否成功
         */
        public boolean reset() {
            try {
                return RedisUtil.set(counterKey, "0");
            } catch (Exception e) {
                logger.error("重置计数器异常: {}", counterKey, e);
                return false;
            }
        }
        
        /**
         * 设置过期时间
         *
         * @param seconds 过期时间（秒）
         * @return 是否成功
         */
        public boolean expire(int seconds) {
            try {
                return RedisUtil.expire(counterKey, seconds);
            } catch (Exception e) {
                logger.error("设置计数器过期时间异常: {}", counterKey, e);
                return false;
            }
        }
    }
    
    /**
     * 滑动窗口计数器
     * 适用于需要统计一段时间内的计数场景，如限流、访问频率控制等
     */
    public static class SlidingWindowCounter {
        private final String counterKey;
        private final int windowSeconds;
        
        /**
         * 创建滑动窗口计数器
         *
         * @param name          计数器名称
         * @param windowSeconds 窗口时间（秒）
         */
        public SlidingWindowCounter(String name, int windowSeconds) {
            this.counterKey = "redis:counter:sliding:" + name;
            this.windowSeconds = windowSeconds;
        }
        
        /**
         * 增加计数
         *
         * @return 当前窗口内的计数
         */
        public long increment() {
            try (Jedis jedis = RedisConfig.getJedis()) {
                long currentTime = System.currentTimeMillis() / 1000;
                Pipeline pipeline = jedis.pipelined();
                
                // 添加当前时间戳作为score，值为当前时间戳（用于去重）
                String member = String.valueOf(System.nanoTime());
                pipeline.zadd(counterKey, currentTime, member);
                
                // 移除窗口之外的数据
                long windowStart = currentTime - windowSeconds;
                pipeline.zremrangeByScore(counterKey, 0, windowStart);
                
                // 获取窗口内的计数
                Response<Long> countResponse = pipeline.zcard(counterKey);
                
                // 设置过期时间，避免长时间不使用的计数器占用内存
                pipeline.expire(counterKey, windowSeconds * 2);
                
                pipeline.sync();
                
                return countResponse.get();
            } catch (Exception e) {
                logger.error("滑动窗口计数异常: {}", counterKey, e);
                return -1;
            }
        }
        
        /**
         * 获取当前窗口内的计数
         *
         * @return 当前窗口内的计数
         */
        public long get() {
            try (Jedis jedis = RedisConfig.getJedis()) {
                long currentTime = System.currentTimeMillis() / 1000;
                Pipeline pipeline = jedis.pipelined();
                
                // 移除窗口之外的数据
                long windowStart = currentTime - windowSeconds;
                pipeline.zremrangeByScore(counterKey, 0, windowStart);
                
                // 获取窗口内的计数
                Response<Long> countResponse = pipeline.zcard(counterKey);
                
                pipeline.sync();
                
                return countResponse.get();
            } catch (Exception e) {
                logger.error("获取滑动窗口计数异常: {}", counterKey, e);
                return 0;
            }
        }
        
        /**
         * 重置计数器
         *
         * @return 是否成功
         */
        public boolean reset() {
            try {
                return RedisUtil.execute(jedis -> {
                    jedis.del(counterKey);
                    return true;
                });
            } catch (Exception e) {
                logger.error("重置滑动窗口计数器异常: {}", counterKey, e);
                return false;
            }
        }
    }
    
    /**
     * 日期计数器
     * 用于按日期统计数据，如每日PV、UV等
     */
    public static class DateCounter {
        private final String counterKeyPrefix;
        
        /**
         * 创建日期计数器
         *
         * @param name 计数器名称
         */
        public DateCounter(String name) {
            this.counterKeyPrefix = "redis:counter:date:" + name + ":";
        }
        
        /**
         * 获取指定日期的计数器key
         */
        private String getCounterKey(LocalDate date) {
            return counterKeyPrefix + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        
        /**
         * 增加指定日期的计数
         *
         * @param date 日期
         * @return 增加后的值
         */
        public long increment(LocalDate date) {
            return increment(date, 1);
        }
        
        /**
         * 增加今天的计数
         *
         * @return 增加后的值
         */
        public long incrementToday() {
            return increment(LocalDate.now(), 1);
        }
        
        /**
         * 增加指定日期的计数
         *
         * @param date  日期
         * @param value 增加的值
         * @return 增加后的值
         */
        public long increment(LocalDate date, long value) {
            try {
                String key = getCounterKey(date);
                return RedisUtil.incrBy(key, value);
            } catch (Exception e) {
                logger.error("增加日期计数异常: {}", date, e);
                return -1;
            }
        }
        
        /**
         * 获取指定日期的计数
         *
         * @param date 日期
         * @return 计数值
         */
        public long get(LocalDate date) {
            try {
                String key = getCounterKey(date);
                String value = RedisUtil.get(key);
                return value == null ? 0 : Long.parseLong(value);
            } catch (Exception e) {
                logger.error("获取日期计数异常: {}", date, e);
                return 0;
            }
        }
        
        /**
         * 获取今天的计数
         *
         * @return 计数值
         */
        public long getToday() {
            return get(LocalDate.now());
        }
        
        /**
         * 获取指定日期范围的计数总和
         *
         * @param startDate 开始日期（包含）
         * @param endDate   结束日期（包含）
         * @return 计数总和
         */
        public long getRange(LocalDate startDate, LocalDate endDate) {
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("开始日期不能晚于结束日期");
            }
            
            long total = 0;
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                total += get(currentDate);
                currentDate = currentDate.plusDays(1);
            }
            
            return total;
        }
        
        /**
         * 设置指定日期的计数过期时间
         *
         * @param date    日期
         * @param seconds 过期时间（秒）
         * @return 是否成功
         */
        public boolean expire(LocalDate date, int seconds) {
            try {
                String key = getCounterKey(date);
                return RedisUtil.expire(key, seconds);
            } catch (Exception e) {
                logger.error("设置日期计数器过期时间异常: {}", date, e);
                return false;
            }
        }
    }
    
    /**
     * 唯一访客计数器（UV）
     * 使用Redis的HyperLogLog实现，适用于大规模数据的基数统计
     */
    public static class UniqueVisitorCounter {
        private final String counterKey;
        
        /**
         * 创建唯一访客计数器
         *
         * @param name 计数器名称
         */
        public UniqueVisitorCounter(String name) {
            this.counterKey = "redis:counter:uv:" + name;
        }
        
        /**
         * 创建带日期的唯一访客计数器
         *
         * @param name 计数器名称
         * @param date 日期
         */
        public UniqueVisitorCounter(String name, LocalDate date) {
            this.counterKey = "redis:counter:uv:" + name + ":" + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        
        /**
         * 添加访客
         *
         * @param visitorId 访客ID
         * @return 是否成功
         */
        public boolean add(String visitorId) {
            try {
                return RedisUtil.execute(jedis -> jedis.pfadd(counterKey, visitorId) == 1);
            } catch (Exception e) {
                logger.error("添加访客异常: {}", counterKey, e);
                return false;
            }
        }
        
        /**
         * 获取唯一访客数量
         *
         * @return 唯一访客数量
         */
        public long count() {
            try {
                return RedisUtil.execute(jedis -> jedis.pfcount(counterKey));
            } catch (Exception e) {
                logger.error("获取唯一访客数量异常: {}", counterKey, e);
                return 0;
            }
        }
        
        /**
         * 合并多个唯一访客计数器
         *
         * @param otherCounters 其他计数器
         * @return 是否成功
         */
        public boolean merge(UniqueVisitorCounter... otherCounters) {
            if (otherCounters == null || otherCounters.length == 0) {
                return true;
            }
            
            try {
                String[] keys = new String[otherCounters.length];
                for (int i = 0; i < otherCounters.length; i++) {
                    keys[i] = otherCounters[i].counterKey;
                }
                
                return RedisUtil.execute(jedis -> jedis.pfmerge(counterKey, keys) != null);
            } catch (Exception e) {
                logger.error("合并唯一访客计数器异常: {}", counterKey, e);
                return false;
            }
        }
        
        /**
         * 设置过期时间
         *
         * @param seconds 过期时间（秒）
         * @return 是否成功
         */
        public boolean expire(int seconds) {
            try {
                return RedisUtil.expire(counterKey, seconds);
            } catch (Exception e) {
                logger.error("设置唯一访客计数器过期时间异常: {}", counterKey, e);
                return false;
            }
        }
    }
}