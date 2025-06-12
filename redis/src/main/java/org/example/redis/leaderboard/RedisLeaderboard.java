package org.example.redis.leaderboard;

import org.example.redis.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis排行榜
 * 基于Redis的有序集合(Sorted Set)实现的排行榜功能
 */
public class RedisLeaderboard {
    private static final Logger logger = LoggerFactory.getLogger(RedisLeaderboard.class);
    
    private final String leaderboardKey;
    private final boolean highToLow; // 是否按分数从高到低排序
    
    /**
     * 排行榜条目
     */
    public static class LeaderboardEntry {
        private final String memberId;
        private final double score;
        private final long rank; // 排名，从1开始
        
        public LeaderboardEntry(String memberId, double score, long rank) {
            this.memberId = memberId;
            this.score = score;
            this.rank = rank;
        }
        
        public String getMemberId() {
            return memberId;
        }
        
        public double getScore() {
            return score;
        }
        
        public long getRank() {
            return rank;
        }
        
        @Override
        public String toString() {
            return "LeaderboardEntry{" +
                    "rank=" + rank +
                    ", memberId='" + memberId + '\'' +
                    ", score=" + score +
                    '}';
        }
    }
    
    /**
     * 创建排行榜
     *
     * @param name      排行榜名称
     * @param highToLow 是否按分数从高到低排序，true表示高分在前（如游戏分数），false表示低分在前（如竞赛用时）
     */
    public RedisLeaderboard(String name, boolean highToLow) {
        this.leaderboardKey = "redis:leaderboard:" + name;
        this.highToLow = highToLow;
    }
    
    /**
     * 创建默认排行榜（按分数从高到低排序）
     *
     * @param name 排行榜名称
     */
    public RedisLeaderboard(String name) {
        this(name, true);
    }
    
    /**
     * 添加或更新成员分数
     *
     * @param memberId 成员ID
     * @param score    分数
     * @return 是否成功
     */
    public boolean addOrUpdate(String memberId, double score) {
        try {
            return RedisUtil.zAdd(leaderboardKey, score, memberId);
        } catch (Exception e) {
            logger.error("添加或更新排行榜成员失败: {}", memberId, e);
            return false;
        }
    }
    
    /**
     * 增加成员分数
     *
     * @param memberId  成员ID
     * @param increment 增加的分数
     * @return 增加后的分数
     */
    public Double incrementScore(String memberId, double increment) {
        try {
            return RedisUtil.zIncrBy(leaderboardKey, increment, memberId);
        } catch (Exception e) {
            logger.error("增加排行榜成员分数失败: {}", memberId, e);
            return null;
        }
    }
    
    /**
     * 获取成员分数
     *
     * @param memberId 成员ID
     * @return 分数，如果成员不存在则返回null
     */
    public Double getScore(String memberId) {
        try {
            return RedisUtil.zScore(leaderboardKey, memberId);
        } catch (Exception e) {
            logger.error("获取排行榜成员分数失败: {}", memberId, e);
            return null;
        }
    }
    
    /**
     * 获取成员排名
     *
     * @param memberId 成员ID
     * @return 排名（从0开始），如果成员不存在则返回null
     */
    public Long getRank(String memberId) {
        try {
            if (highToLow) {
                return RedisUtil.zRevRank(leaderboardKey, memberId);
            } else {
                return RedisUtil.zRank(leaderboardKey, memberId);
            }
        } catch (Exception e) {
            logger.error("获取排行榜成员排名失败: {}", memberId, e);
            return null;
        }
    }
    
    /**
     * 移除成员
     *
     * @param memberId 成员ID
     * @return 是否成功
     */
    public boolean remove(String memberId) {
        try {
            return RedisUtil.zRem(leaderboardKey, memberId) > 0;
        } catch (Exception e) {
            logger.error("移除排行榜成员失败: {}", memberId, e);
            return false;
        }
    }
    
    /**
     * 获取排行榜总成员数
     *
     * @return 成员数量
     */
    public long count() {
        try {
            return RedisUtil.zCard(leaderboardKey);
        } catch (Exception e) {
            logger.error("获取排行榜成员数量失败", e);
            return 0;
        }
    }
    
    /**
     * 获取排行榜前N名
     *
     * @param count 获取的数量
     * @return 排行榜条目列表
     */
    public List<LeaderboardEntry> getTop(int count) {
        return getRange(0, count - 1);
    }
    
    /**
     * 获取排行榜指定范围
     *
     * @param start 起始位置（从0开始）
     * @param stop  结束位置
     * @return 排行榜条目列表
     */
    public List<LeaderboardEntry> getRange(long start, long stop) {
        try {
            List<Tuple> tuples;
            if (highToLow) {
                // 按分数从高到低获取
                tuples = RedisUtil.zRevRangeWithScores(leaderboardKey, start, stop);
            } else {
                // 按分数从低到高获取
                tuples = RedisUtil.zRangeWithScores(leaderboardKey, start, stop);
            }
            
            List<LeaderboardEntry> entries = new ArrayList<>();
            long rank = start + 1; // 排名从1开始
            
            for (Tuple tuple : tuples) {
                entries.add(new LeaderboardEntry(
                        tuple.getElement(),
                        tuple.getScore(),
                        rank++
                ));
            }
            
            return entries;
        } catch (Exception e) {
            logger.error("获取排行榜范围失败", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * 获取成员附近的排名
     *
     * @param memberId 成员ID
     * @param count    获取的数量（包括成员本身）
     * @return 排行榜条目列表
     */
    public List<LeaderboardEntry> getAroundMember(String memberId, int count) {
        try {
            // 获取成员排名
            Long rank = getRank(memberId);
            if (rank == null) {
                return java.util.Collections.emptyList();
            }
            
            // 计算要获取的范围
            int before = count / 2;
            long start = Math.max(0, rank - before);
            long stop = start + count - 1;
            
            return getRange(start, stop);
        } catch (Exception e) {
            logger.error("获取成员附近排名失败: {}", memberId, e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * 获取指定分数范围的成员
     *
     * @param minScore 最小分数
     * @param maxScore 最大分数
     * @return 排行榜条目列表
     */
    public List<LeaderboardEntry> getByScoreRange(double minScore, double maxScore) {
        try {
            List<Tuple> tuples;
            if (highToLow) {
                // 按分数从高到低获取，注意参数顺序需要调整
                tuples = RedisUtil.zRevRangeByScoreWithScores(leaderboardKey, maxScore, minScore);
            } else {
                tuples = RedisUtil.zRangeByScoreWithScores(leaderboardKey, minScore, maxScore);
            }
            
            List<LeaderboardEntry> entries = new ArrayList<>();
            for (Tuple tuple : tuples) {
                // 获取成员排名
                Long rank = getRank(tuple.getElement());
                if (rank == null) {
                    continue;
                }
                
                entries.add(new LeaderboardEntry(
                        tuple.getElement(),
                        tuple.getScore(),
                        rank + 1 // 排名从1开始
                ));
            }
            
            return entries;
        } catch (Exception e) {
            logger.error("获取指定分数范围的成员失败", e);
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * 清空排行榜
     *
     * @return 是否成功
     */
    public boolean clear() {
        try {
            return RedisUtil.delete(leaderboardKey);
        } catch (Exception e) {
            logger.error("清空排行榜失败", e);
            return false;
        }
    }
    
    /**
     * 设置排行榜过期时间
     *
     * @param seconds 过期时间（秒）
     * @return 是否成功
     */
    public boolean expire(int seconds) {
        try {
            return RedisUtil.expire(leaderboardKey, seconds);
        } catch (Exception e) {
            logger.error("设置排行榜过期时间失败", e);
            return false;
        }
    }
}