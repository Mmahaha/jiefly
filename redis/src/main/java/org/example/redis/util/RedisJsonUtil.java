package org.example.redis.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Redis JSON工具类，用于在Redis中存储和获取JSON对象
 */
public class RedisJsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisJsonUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象转换为JSON字符串并存储到Redis
     *
     * @param key   Redis键
     * @param value 要存储的对象
     * @param <T>   对象类型
     * @return 是否成功
     */
    public static <T> boolean setJson(String key, T value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            return RedisUtil.set(key, jsonValue);
        } catch (JsonProcessingException e) {
            logger.error("对象转JSON失败", e);
            return false;
        }
    }

    /**
     * 将对象转换为JSON字符串并存储到Redis，同时设置过期时间
     *
     * @param key     Redis键
     * @param value   要存储的对象
     * @param seconds 过期时间（秒）
     * @param <T>     对象类型
     * @return 是否成功
     */
    public static <T> boolean setJsonEx(String key, T value, int seconds) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            return RedisUtil.setEx(key, jsonValue, seconds);
        } catch (JsonProcessingException e) {
            logger.error("对象转JSON失败", e);
            return false;
        }
    }

    /**
     * 从Redis获取JSON字符串并转换为对象
     *
     * @param key   Redis键
     * @param clazz 对象类型的Class
     * @param <T>   对象类型
     * @return 对象实例，如果不存在或转换失败则返回null
     */
    public static <T> T getJson(String key, Class<T> clazz) {
        String json = RedisUtil.get(key);
        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("JSON转对象失败", e);
            return null;
        }
    }

    /**
     * 从Redis获取JSON字符串并转换为复杂类型对象（如List、Map等）
     *
     * @param key          Redis键
     * @param typeReference 类型引用，例如：new TypeReference<List<User>>(){}
     * @param <T>          对象类型
     * @return 对象实例，如果不存在或转换失败则返回null
     */
    public static <T> T getJson(String key, TypeReference<T> typeReference) {
        String json = RedisUtil.get(key);
        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            logger.error("JSON转对象失败", e);
            return null;
        }
    }

    /**
     * 将对象转换为JSON字符串并存储到Redis哈希表的字段中
     *
     * @param key   Redis键
     * @param field 哈希表字段
     * @param value 要存储的对象
     * @param <T>   对象类型
     * @return 是否成功
     */
    public static <T> boolean hSetJson(String key, String field, T value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            return RedisUtil.hSet(key, field, jsonValue);
        } catch (JsonProcessingException e) {
            logger.error("对象转JSON失败", e);
            return false;
        }
    }

    /**
     * 从Redis哈希表获取JSON字符串并转换为对象
     *
     * @param key   Redis键
     * @param field 哈希表字段
     * @param clazz 对象类型的Class
     * @param <T>   对象类型
     * @return 对象实例，如果不存在或转换失败则返回null
     */
    public static <T> T hGetJson(String key, String field, Class<T> clazz) {
        String json = RedisUtil.hGet(key, field);
        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("JSON转对象失败", e);
            return null;
        }
    }

    /**
     * 从Redis哈希表获取JSON字符串并转换为复杂类型对象（如List、Map等）
     *
     * @param key          Redis键
     * @param field        哈希表字段
     * @param typeReference 类型引用，例如：new TypeReference<List<User>>(){}
     * @param <T>          对象类型
     * @return 对象实例，如果不存在或转换失败则返回null
     */
    public static <T> T hGetJson(String key, String field, TypeReference<T> typeReference) {
        String json = RedisUtil.hGet(key, field);
        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            logger.error("JSON转对象失败", e);
            return null;
        }
    }
}