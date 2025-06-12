package org.example.redis.util;

import org.example.redis.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis工具类，封装Redis的基本操作
 */
public class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * 执行Redis操作的函数式接口
     */
    @FunctionalInterface
    public interface JedisOperation<T> {
        T execute(Jedis jedis);
    }

    /**
     * 执行Redis操作并自动关闭连接
     */
    public static <T> T execute(JedisOperation<T> operation) {
        try (Jedis jedis = RedisConfig.getJedis()) {
            return operation.execute(jedis);
        } catch (Exception e) {
            logger.error("执行Redis操作失败", e);
            throw new RuntimeException("执行Redis操作失败", e);
        }
    }

    // ================= Key操作 =================

    /**
     * 设置key的过期时间
     */
    public static boolean expire(String key, int seconds) {
        return execute(jedis -> jedis.expire(key, seconds) == 1);
    }

    /**
     * 获取key的过期时间
     */
    public static long getExpire(String key) {
        return execute(jedis -> jedis.ttl(key));
    }

    /**
     * 判断key是否存在
     */
    public static boolean exists(String key) {
        return execute(jedis -> jedis.exists(key));
    }

    /**
     * 删除key
     */
    public static boolean delete(String key) {
        return execute(jedis -> jedis.del(key) == 1);
    }

    /**
     * 批量删除key
     */
    public static long delete(String... keys) {
        return execute(jedis -> jedis.del(keys));
    }

    /**
     * 按模式获取key
     */
    public static Set<String> keys(String pattern) {
        return execute(jedis -> jedis.keys(pattern));
    }

    /**
     * 使用scan命令按模式获取key（推荐用于生产环境，keys命令可能会阻塞Redis）
     */
    public static Set<String> scan(String pattern, int count) {
        return execute(jedis -> {
            Set<String> keys = new java.util.HashSet<>();
            String cursor = "0";
            ScanParams scanParams = new ScanParams().match(pattern).count(count);
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                keys.addAll(scanResult.getResult());
                cursor = scanResult.getCursor();
            } while (!cursor.equals("0"));
            return keys;
        });
    }

    // ================= String操作 =================

    /**
     * 设置字符串值
     */
    public static boolean set(String key, String value) {
        return execute(jedis -> jedis.set(key, value).equals("OK"));
    }

    /**
     * 设置字符串值并设置过期时间
     */
    public static boolean setEx(String key, String value, int seconds) {
        return execute(jedis -> jedis.setex(key, seconds, value).equals("OK"));
    }

    /**
     * 只有当key不存在时才设置值（原子操作）
     */
    public static boolean setNx(String key, String value) {
        return execute(jedis -> jedis.setnx(key, value) == 1);
    }

    /**
     * 获取字符串值
     */
    public static String get(String key) {
        return execute(jedis -> jedis.get(key));
    }

    /**
     * 将key对应的值加1
     */
    public static long incr(String key) {
        return execute(jedis -> jedis.incr(key));
    }

    /**
     * 将key对应的值加上指定的增量
     */
    public static long incrBy(String key, long increment) {
        return execute(jedis -> jedis.incrBy(key, increment));
    }

    /**
     * 将key对应的值减1
     */
    public static long decr(String key) {
        return execute(jedis -> jedis.decr(key));
    }

    /**
     * 将key对应的值减去指定的减量
     */
    public static long decrBy(String key, long decrement) {
        return execute(jedis -> jedis.decrBy(key, decrement));
    }

    // ================= Hash操作 =================

    /**
     * 设置哈希表字段值
     */
    public static boolean hSet(String key, String field, String value) {
        return execute(jedis -> jedis.hset(key, field, value) == 1);
    }

    /**
     * 批量设置哈希表字段值
     */
    public static boolean hMSet(String key, Map<String, String> hash) {
        return execute(jedis -> jedis.hmset(key, hash).equals("OK"));
    }

    /**
     * 获取哈希表字段值
     */
    public static String hGet(String key, String field) {
        return execute(jedis -> jedis.hget(key, field));
    }

    /**
     * 批量获取哈希表字段值
     */
    public static List<String> hMGet(String key, String... fields) {
        return execute(jedis -> jedis.hmget(key, fields));
    }

    /**
     * 获取哈希表所有字段
     */
    public static Set<String> hKeys(String key) {
        return execute(jedis -> jedis.hkeys(key));
    }

    /**
     * 获取哈希表所有值
     */
    public static List<String> hVals(String key) {
        return execute(jedis -> jedis.hvals(key));
    }

    /**
     * 获取哈希表所有字段和值
     */
    public static Map<String, String> hGetAll(String key) {
        return execute(jedis -> jedis.hgetAll(key));
    }

    /**
     * 删除哈希表字段
     */
    public static boolean hDel(String key, String... fields) {
        return execute(jedis -> jedis.hdel(key, fields) > 0);
    }

    /**
     * 判断哈希表字段是否存在
     */
    public static boolean hExists(String key, String field) {
        return execute(jedis -> jedis.hexists(key, field));
    }

    /**
     * 哈希表字段值加上指定的增量
     */
    public static long hIncrBy(String key, String field, long increment) {
        return execute(jedis -> jedis.hincrBy(key, field, increment));
    }

    // ================= List操作 =================

    /**
     * 将一个值插入到列表头部
     */
    public static long lPush(String key, String... values) {
        return execute(jedis -> jedis.lpush(key, values));
    }

    /**
     * 将一个值插入到列表尾部
     */
    public static long rPush(String key, String... values) {
        return execute(jedis -> jedis.rpush(key, values));
    }

    /**
     * 获取列表指定范围内的元素
     */
    public static List<String> lRange(String key, long start, long stop) {
        return execute(jedis -> jedis.lrange(key, start, stop));
    }

    /**
     * 通过索引获取列表中的元素
     */
    public static String lIndex(String key, long index) {
        return execute(jedis -> jedis.lindex(key, index));
    }

    /**
     * 获取列表长度
     */
    public static long lLen(String key) {
        return execute(jedis -> jedis.llen(key));
    }

    /**
     * 移除并获取列表的第一个元素
     */
    public static String lPop(String key) {
        return execute(jedis -> jedis.lpop(key));
    }

    /**
     * 移除并获取列表的最后一个元素
     */
    public static String rPop(String key) {
        return execute(jedis -> jedis.rpop(key));
    }

    /**
     * 移出并获取列表的第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     */
    public static List<String> bLPop(int timeout, String key) {
        return execute(jedis -> jedis.blpop(timeout, key));
    }

    /**
     * 移出并获取列表的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     */
    public static List<String> bRPop(int timeout, String key) {
        return execute(jedis -> jedis.brpop(timeout, key));
    }

    // ================= Set操作 =================

    /**
     * 向集合添加一个或多个成员
     */
    public static long sAdd(String key, String... members) {
        return execute(jedis -> jedis.sadd(key, members));
    }

    /**
     * 获取集合的成员数
     */
    public static long sCard(String key) {
        return execute(jedis -> jedis.scard(key));
    }

    /**
     * 判断成员是否是集合的成员
     */
    public static boolean sIsMember(String key, String member) {
        return execute(jedis -> jedis.sismember(key, member));
    }

    /**
     * 获取集合所有成员
     */
    public static Set<String> sMembers(String key) {
        return execute(jedis -> jedis.smembers(key));
    }

    /**
     * 移除集合中一个或多个成员
     */
    public static long sRem(String key, String... members) {
        return execute(jedis -> jedis.srem(key, members));
    }

    /**
     * 返回给定所有集合的交集
     */
    public static Set<String> sInter(String... keys) {
        return execute(jedis -> jedis.sinter(keys));
    }

    /**
     * 返回给定所有集合的并集
     */
    public static Set<String> sUnion(String... keys) {
        return execute(jedis -> jedis.sunion(keys));
    }

    /**
     * 返回给定所有集合的差集
     */
    public static Set<String> sDiff(String... keys) {
        return execute(jedis -> jedis.sdiff(keys));
    }

    /**
     * 随机获取集合中的一个成员
     */
    public static String sRandMember(String key) {
        return execute(jedis -> jedis.srandmember(key));
    }

    // ================= Sorted Set操作 =================

    /**
     * 向有序集合添加一个成员，或者更新已存在成员的分数
     */
    public static boolean zAdd(String key, double score, String member) {
        return execute(jedis -> jedis.zadd(key, score, member) == 1);
    }

    /**
     * 获取有序集合的成员数
     */
    public static long zCard(String key) {
        return execute(jedis -> jedis.zcard(key));
    }

    /**
     * 获取有序集合中指定成员的分数
     */
    public static Double zScore(String key, String member) {
        return execute(jedis -> jedis.zscore(key, member));
    }

    /**
     * 获取有序集合中指定分数区间的成员
     */
    public static List<String> zRangeByScore(String key, double min, double max) {
        return execute(jedis -> jedis.zrangeByScore(key, min, max));
    }

    /**
     * 获取有序集合中指定分数区间的成员和分数
     */
    public static List<Tuple> zRangeByScoreWithScores(String key, double min, double max) {
        return execute(jedis -> jedis.zrangeByScoreWithScores(key, min, max));
    }
    
    /**
     * 获取有序集合中指定分数区间的成员（按分数从高到低）
     */
    public static List<String> zRevRangeByScore(String key, double max, double min) {
        return execute(jedis -> jedis.zrevrangeByScore(key, max, min));
    }

    /**
     * 获取有序集合中指定分数区间的成员和分数（按分数从高到低）
     */
    public static List<Tuple> zRevRangeByScoreWithScores(String key, double max, double min) {
        return execute(jedis -> jedis.zrevrangeByScoreWithScores(key, max, min));
    }

    /**
     * 获取有序集合中指定索引区间的成员
     */
    public static List<String> zRange(String key, long start, long stop) {
        return execute(jedis -> jedis.zrange(key, start, stop));
    }

    /**
     * 获取有序集合中指定索引区间的成员和分数
     */
    public static List<Tuple> zRangeWithScores(String key, long start, long stop) {
        return execute(jedis -> jedis.zrangeWithScores(key, start, stop));
    }
    
    /**
     * 获取有序集合中指定索引区间的成员（按分数从高到低）
     */
    public static List<String> zRevRange(String key, long start, long stop) {
        return execute(jedis -> jedis.zrevrange(key, start, stop));
    }

    /**
     * 获取有序集合中指定索引区间的成员和分数（按分数从高到低）
     */
    public static List<Tuple> zRevRangeWithScores(String key, long start, long stop) {
        return execute(jedis -> jedis.zrevrangeWithScores(key, start, stop));
    }

    /**
     * 移除有序集合中的一个或多个成员
     */
    public static long zRem(String key, String... members) {
        return execute(jedis -> jedis.zrem(key, members));
    }

    /**
     * 获取有序集合中成员的排名（从小到大排序）
     */
    public static Long zRank(String key, String member) {
        return execute(jedis -> jedis.zrank(key, member));
    }

    /**
     * 获取有序集合中成员的排名（从大到小排序）
     */
    public static Long zRevRank(String key, String member) {
        return execute(jedis -> jedis.zrevrank(key, member));
    }

    /**
     * 为有序集合中的成员增加分数
     */
    public static Double zIncrBy(String key, double increment, String member) {
        return execute(jedis -> jedis.zincrby(key, increment, member));
    }
}