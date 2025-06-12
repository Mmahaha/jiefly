package org.example.redis.geo;

import org.example.redis.config.RedisConfig;
import org.example.redis.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.resps.GeoRadiusResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis地理位置工具类
 * 基于Redis的GEO功能实现，用于地理位置相关的应用场景
 */
public class RedisGeoUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisGeoUtil.class);
    
    /**
     * 地理位置信息
     */
    public static class GeoLocation {
        private final String id;
        private final double longitude;
        private final double latitude;
        private final String name;
        private Double distance; // 距离，单位：米
        
        public GeoLocation(String id, double longitude, double latitude, String name) {
            this.id = id;
            this.longitude = longitude;
            this.latitude = latitude;
            this.name = name;
        }
        
        public String getId() {
            return id;
        }
        
        public double getLongitude() {
            return longitude;
        }
        
        public double getLatitude() {
            return latitude;
        }
        
        public String getName() {
            return name;
        }
        
        public Double getDistance() {
            return distance;
        }
        
        public void setDistance(Double distance) {
            this.distance = distance;
        }
        
        @Override
        public String toString() {
            return "GeoLocation{" +
                    "id='" + id + '\'' +
                    ", longitude=" + longitude +
                    ", latitude=" + latitude +
                    ", name='" + name + '\'' +
                    ", distance=" + (distance != null ? distance + "m" : "unknown") +
                    '}';
        }
    }
    
    /**
     * 地理位置管理器
     */
    public static class GeoManager {
        private final String geoKey;
        private final String infoKey; // 用于存储位置的附加信息
        
        /**
         * 创建地理位置管理器
         *
         * @param name 地理位置集合名称
         */
        public GeoManager(String name) {
            this.geoKey = "redis:geo:" + name;
            this.infoKey = "redis:geo:info:" + name;
        }
        
        /**
         * 添加地理位置
         *
         * @param location 地理位置信息
         * @return 是否成功
         */
        public boolean add(GeoLocation location) {
            try {
                // 添加地理位置坐标
                long result = RedisUtil.execute(jedis ->
                        jedis.geoadd(geoKey, location.getLongitude(), location.getLatitude(), location.getId())
                );
                
                // 存储位置的附加信息
                RedisUtil.hSet(infoKey, location.getId(), location.getName());
                
                return result > 0;
            } catch (Exception e) {
                logger.error("添加地理位置失败: {}", location.getId(), e);
                return false;
            }
        }
        
        /**
         * 批量添加地理位置
         *
         * @param locations 地理位置信息列表
         * @return 成功添加的数量
         */
        public long addAll(List<GeoLocation> locations) {
            if (locations == null || locations.isEmpty()) {
                return 0;
            }
            
            try {
                Map<String, GeoCoordinate> memberCoordinateMap = new HashMap<>();
                Map<String, String> memberInfoMap = new HashMap<>();
                
                for (GeoLocation location : locations) {
                    memberCoordinateMap.put(location.getId(), new GeoCoordinate(location.getLongitude(), location.getLatitude()));
                    memberInfoMap.put(location.getId(), location.getName());
                }
                
                // 批量添加地理位置坐标
                long result = RedisUtil.execute(jedis -> jedis.geoadd(geoKey, memberCoordinateMap));
                
                // 批量存储位置的附加信息
                RedisUtil.hMSet(infoKey, memberInfoMap);
                
                return result;
            } catch (Exception e) {
                logger.error("批量添加地理位置失败", e);
                return 0;
            }
        }
        
        /**
         * 移除地理位置
         *
         * @param id 位置ID
         * @return 是否成功
         */
        public boolean remove(String id) {
            try {
                // 移除地理位置坐标
                long result = RedisUtil.execute(jedis -> jedis.zrem(geoKey, id));
                
                // 移除位置的附加信息
                RedisUtil.hDel(infoKey, id);
                
                return result > 0;
            } catch (Exception e) {
                logger.error("移除地理位置失败: {}", id, e);
                return false;
            }
        }
        
        /**
         * 获取地理位置的坐标
         *
         * @param id 位置ID
         * @return 坐标，如果不存在则返回null
         */
        public GeoCoordinate getCoordinate(String id) {
            try {
                List<GeoCoordinate> positions = RedisUtil.execute(jedis -> jedis.geopos(geoKey, id));
                if (positions != null && !positions.isEmpty() && positions.get(0) != null) {
                    return positions.get(0);
                }
                return null;
            } catch (Exception e) {
                logger.error("获取地理位置坐标失败: {}", id, e);
                return null;
            }
        }
        
        /**
         * 获取地理位置信息
         *
         * @param id 位置ID
         * @return 地理位置信息，如果不存在则返回null
         */
        public GeoLocation getLocation(String id) {
            try {
                GeoCoordinate coordinate = getCoordinate(id);
                if (coordinate == null) {
                    return null;
                }
                
                String name = RedisUtil.hGet(infoKey, id);
                return new GeoLocation(id, coordinate.getLongitude(), coordinate.getLatitude(), name);
            } catch (Exception e) {
                logger.error("获取地理位置信息失败: {}", id, e);
                return null;
            }
        }
        
        /**
         * 计算两个位置之间的距离
         *
         * @param id1  位置1的ID
         * @param id2  位置2的ID
         * @param unit 距离单位
         * @return 距离，如果位置不存在则返回null
         */
        public Double getDistance(String id1, String id2, GeoUnit unit) {
            try {
                return RedisUtil.execute(jedis -> jedis.geodist(geoKey, id1, id2, unit));
            } catch (Exception e) {
                logger.error("计算地理位置距离失败: {} - {}", id1, id2, e);
                return null;
            }
        }
        
        /**
         * 查找指定范围内的位置
         *
         * @param longitude 中心点经度
         * @param latitude  中心点纬度
         * @param radius    半径
         * @param unit      距离单位
         * @param count     返回的最大数量
         * @return 范围内的位置列表
         */
        public List<GeoLocation> findNearby(double longitude, double latitude, double radius, GeoUnit unit, int count) {
            try {
                // 设置查询参数：返回距离、坐标，并按距离排序
                GeoRadiusParam param = GeoRadiusParam.geoRadiusParam()
                        .withDist()
                        .withCoord()
                        .sortAscending()
                        .count(count);
                
                // 执行范围查询
                List<GeoRadiusResponse> responses = RedisUtil.execute(jedis ->
                        jedis.georadius(geoKey, longitude, latitude, radius, unit, param)
                );
                
                // 转换结果
                return responses.stream().map(response -> {
                    String id = response.getMemberByString();
                    String name = RedisUtil.hGet(infoKey, id);
                    GeoCoordinate coordinate = response.getCoordinate();
                    
                    GeoLocation location = new GeoLocation(
                            id,
                            coordinate.getLongitude(),
                            coordinate.getLatitude(),
                            name
                    );
                    location.setDistance(response.getDistance());
                    
                    return location;
                }).toList();
            } catch (Exception e) {
                logger.error("查找附近位置失败", e);
                return java.util.Collections.emptyList();
            }
        }
        
        /**
         * 查找指定位置附近的其他位置
         *
         * @param id     中心点位置ID
         * @param radius 半径
         * @param unit   距离单位
         * @param count  返回的最大数量
         * @return 范围内的位置列表
         */
        public List<GeoLocation> findNearbyById(String id, double radius, GeoUnit unit, int count) {
            try {
                // 设置查询参数：返回距离、坐标，并按距离排序
                GeoRadiusParam param = GeoRadiusParam.geoRadiusParam()
                        .withDist()
                        .withCoord()
                        .sortAscending()
                        .count(count);
                
                // 执行范围查询
                List<GeoRadiusResponse> responses = RedisUtil.execute(jedis ->
                        jedis.georadiusByMember(geoKey, id, radius, unit, param)
                );
                
                // 转换结果
                return responses.stream()
                        .filter(response -> !response.getMemberByString().equals(id)) // 排除自己
                        .map(response -> {
                            String memberId = response.getMemberByString();
                            String name = RedisUtil.hGet(infoKey, memberId);
                            GeoCoordinate coordinate = response.getCoordinate();
                            
                            GeoLocation location = new GeoLocation(
                                    memberId,
                                    coordinate.getLongitude(),
                                    coordinate.getLatitude(),
                                    name
                            );
                            location.setDistance(response.getDistance());
                            
                            return location;
                        }).toList();
            } catch (Exception e) {
                logger.error("查找附近位置失败: {}", id, e);
                return java.util.Collections.emptyList();
            }
        }
        
        /**
         * 获取位置的GeoHash值
         *
         * @param id 位置ID
         * @return GeoHash值
         */
        public String getGeoHash(String id) {
            try {
                List<String> hashes = RedisUtil.execute(jedis -> jedis.geohash(geoKey, id));
                if (hashes != null && !hashes.isEmpty()) {
                    return hashes.get(0);
                }
                return null;
            } catch (Exception e) {
                logger.error("获取GeoHash失败: {}", id, e);
                return null;
            }
        }
    }
}