package org.example.redis.ratelimit;

import org.example.redis.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.time.Instant;

/**
 * Redis限流器
 * 提供固定窗口和滑动窗口两种限流算法实现
 */
public class RedisRateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimiter.class);
    
    // 限流器前缀
    private static final String RATE_LIMITER_PREFIX = "redis:ratelimit:";
    
    /**
     * 固定窗口限流算法
     * 在固定的时间窗口内限制请求数量
     */
    public static class FixedWindow {
        private final String key;
        private final int maxRequests;
        private final int windowSeconds;
        
        /**
         * 创建固定窗口限流器
         *
         * @param name          限流器名称
         * @param maxRequests   窗口内最大请求数
         * @param windowSeconds 窗口时间（秒）
         */
        public FixedWindow(String name, int maxRequests, int windowSeconds) {
            this.key = RATE_LIMITER_PREFIX + "fixed:" + name;
            this.maxRequests = maxRequests;
            this.windowSeconds = windowSeconds;
        }
        
        /**
         * 尝试获取令牌
         *
         * @return 是否获取成功
         */
        public boolean tryAcquire() {
            try (Jedis jedis = RedisConfig.getJedis()) {
                // 获取当前时间戳（秒）
                long currentTimestamp = Instant.now().getEpochSecond();
                // 计算当前窗口的开始时间
                long windowStart = currentTimestamp - (currentTimestamp % windowSeconds);
                // 构建当前窗口的key
                String windowKey = key + ":" + windowStart;
                
                Pipeline pipeline = jedis.pipelined();
                // 增加计数
                Response<Long> countResponse = pipeline.incr(windowKey);
                // 设置过期时间（窗口时间 + 1秒，确保窗口结束后自动删除）
                pipeline.expire(windowKey, windowSeconds + 1);
                pipeline.sync();
                
                long count = countResponse.get();
                boolean allowed = count <= maxRequests;
                
                if (!allowed) {
                    logger.debug("限流：{} 超过最大请求数 {}/{}", key, count, maxRequests);
                }
                
                return allowed;
            } catch (Exception e) {
                logger.error("限流器异常", e);
                // 发生异常时默认放行
                return true;
            }
        }
    }
    
    /**
     * 滑动窗口限流算法
     * 使用Redis的有序集合实现滑动窗口限流
     */
    public static class SlidingWindow {
        private final String key;
        private final int maxRequests;
        private final int windowSeconds;
        
        /**
         * 创建滑动窗口限流器
         *
         * @param name          限流器名称
         * @param maxRequests   窗口内最大请求数
         * @param windowSeconds 窗口时间（秒）
         */
        public SlidingWindow(String name, int maxRequests, int windowSeconds) {
            this.key = RATE_LIMITER_PREFIX + "sliding:" + name;
            this.maxRequests = maxRequests;
            this.windowSeconds = windowSeconds;
        }
        
        /**
         * 尝试获取令牌
         *
         * @return 是否获取成功
         */
        public boolean tryAcquire() {
            try (Jedis jedis = RedisConfig.getJedis()) {
                long currentTimestamp = Instant.now().getEpochSecond();
                // 计算窗口的开始时间
                long windowStart = currentTimestamp - windowSeconds;
                
                Pipeline pipeline = jedis.pipelined();
                // 移除窗口之前的所有请求
                pipeline.zremrangeByScore(key, 0, windowStart);
                // 添加当前请求，分数为当前时间戳
                String requestId = String.valueOf(System.nanoTime());
                pipeline.zadd(key, currentTimestamp, requestId);
                // 获取窗口内的请求数
                Response<Long> countResponse = pipeline.zcard(key);
                // 设置过期时间
                pipeline.expire(key, windowSeconds * 2);
                pipeline.sync();
                
                long count = countResponse.get();
                boolean allowed = count <= maxRequests;
                
                if (!allowed) {
                    logger.debug("限流：{} 超过最大请求数 {}/{}", key, count, maxRequests);
                }
                
                return allowed;
            } catch (Exception e) {
                logger.error("限流器异常", e);
                // 发生异常时默认放行
                return true;
            }
        }
    }
    
    /**
     * 令牌桶限流算法
     * 使用Redis实现令牌桶限流
     */
    public static class TokenBucket {
        private final String key;
        private final int capacity;
        private final int refillRate;
        
        /**
         * 创建令牌桶限流器
         *
         * @param name       限流器名称
         * @param capacity   桶容量（最大令牌数）
         * @param refillRate 令牌填充速率（每秒）
         */
        public TokenBucket(String name, int capacity, int refillRate) {
            this.key = RATE_LIMITER_PREFIX + "token:" + name;
            this.capacity = capacity;
            this.refillRate = refillRate;
        }
        
        /**
         * 尝试获取令牌
         *
         * @return 是否获取成功
         */
        public boolean tryAcquire() {
            return tryAcquire(1);
        }
        
        /**
         * 尝试获取指定数量的令牌
         *
         * @param tokens 需要的令牌数
         * @return 是否获取成功
         */
        public boolean tryAcquire(int tokens) {
            if (tokens <= 0) {
                return true;
            }
            
            try (Jedis jedis = RedisConfig.getJedis()) {
                long currentTime = System.currentTimeMillis();
                String lastRefillTimeKey = key + ":lastRefillTime";
                String tokensKey = key + ":tokens";
                
                // 使用Lua脚本确保原子性
                String script = ""
                        + "local lastRefillTime = tonumber(redis.call('get', KEYS[1])) or 0 \n"
                        + "local currentTokens = tonumber(redis.call('get', KEYS[2])) or ARGV[1] \n"
                        + "local currentTime = tonumber(ARGV[2]) \n"
                        + "local capacity = tonumber(ARGV[1]) \n"
                        + "local refillRate = tonumber(ARGV[3]) \n"
                        + "local requestTokens = tonumber(ARGV[4]) \n"
                        + "local elapsedTime = currentTime - lastRefillTime \n"
                        + "local tokensToAdd = math.floor(elapsedTime / 1000 * refillRate) \n"
                        + "if tokensToAdd > 0 then \n"
                        + "  currentTokens = math.min(capacity, currentTokens + tokensToAdd) \n"
                        + "  redis.call('set', KEYS[1], currentTime) \n"
                        + "end \n"
                        + "if currentTokens >= requestTokens then \n"
                        + "  redis.call('set', KEYS[2], currentTokens - requestTokens) \n"
                        + "  return 1 \n"
                        + "else \n"
                        + "  return 0 \n"
                        + "end";
                
                Object result = jedis.eval(
                        script,
                        2,
                        lastRefillTimeKey, tokensKey,
                        String.valueOf(capacity),
                        String.valueOf(currentTime),
                        String.valueOf(refillRate),
                        String.valueOf(tokens)
                );
                
                boolean allowed = "1".equals(result.toString());
                
                if (!allowed) {
                    logger.debug("限流：{} 令牌不足", key);
                }
                
                // 设置过期时间，避免长时间不使用的限流器占用内存
                jedis.expire(lastRefillTimeKey, 3600);
                jedis.expire(tokensKey, 3600);
                
                return allowed;
            } catch (Exception e) {
                logger.error("限流器异常", e);
                // 发生异常时默认放行
                return true;
            }
        }
    }
}