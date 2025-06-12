package org.example.redis.mq;

import org.example.redis.config.RedisConfig;
import org.example.redis.util.RedisJsonUtil;
import org.example.redis.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Redis消息队列
 * 提供基于List和Pub/Sub两种实现方式
 */
public class RedisMessageQueue {
    private static final Logger logger = LoggerFactory.getLogger(RedisMessageQueue.class);
    
    /**
     * 基于Redis List实现的消息队列
     */
    public static class ListQueue {
        private final String queueKey;
        
        /**
         * 创建基于List的消息队列
         *
         * @param queueName 队列名称
         */
        public ListQueue(String queueName) {
            this.queueKey = "redis:mq:list:" + queueName;
        }
        
        /**
         * 发送消息到队列
         *
         * @param message 消息内容
         * @return 是否成功
         */
        public boolean send(String message) {
            try {
                long result = RedisUtil.rPush(queueKey, message);
                return result > 0;
            } catch (Exception e) {
                logger.error("发送消息到队列失败: {}", queueKey, e);
                return false;
            }
        }
        
        /**
         * 发送对象消息到队列（自动序列化为JSON）
         *
         * @param message 消息对象
         * @param <T>     消息类型
         * @return 是否成功
         */
        public <T> boolean sendObject(T message) {
            try {
                String jsonMessage = RedisUtil.execute(jedis -> {
                    try {
                        return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(message);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return send(jsonMessage);
            } catch (Exception e) {
                logger.error("发送对象消息到队列失败: {}", queueKey, e);
                return false;
            }
        }
        
        /**
         * 从队列接收消息（阻塞方式）
         *
         * @param timeout 超时时间（秒），0表示永久阻塞
         * @return 消息内容，如果超时则返回null
         */
        public String receive(int timeout) {
            try {
                List<String> result = RedisUtil.bLPop(timeout, queueKey);
                if (result != null && result.size() >= 2) {
                    // bLPop返回的结果中第一个元素是键名，第二个元素是值
                    return result.get(1);
                }
                return null;
            } catch (Exception e) {
                logger.error("从队列接收消息失败: {}", queueKey, e);
                return null;
            }
        }
        
        /**
         * 从队列接收对象消息（阻塞方式）
         *
         * @param timeout 超时时间（秒），0表示永久阻塞
         * @param clazz   对象类型
         * @param <T>     消息类型
         * @return 消息对象，如果超时或转换失败则返回null
         */
        public <T> T receiveObject(int timeout, Class<T> clazz) {
            String jsonMessage = receive(timeout);
            if (jsonMessage == null) {
                return null;
            }
            
            try {
                return new com.fasterxml.jackson.databind.ObjectMapper().readValue(jsonMessage, clazz);
            } catch (Exception e) {
                logger.error("解析对象消息失败", e);
                return null;
            }
        }
        
        /**
         * 获取队列长度
         *
         * @return 队列中的消息数量
         */
        public long size() {
            return RedisUtil.lLen(queueKey);
        }
        
        /**
         * 启动消息处理器
         *
         * @param handler      消息处理器
         * @param threadCount  处理线程数
         * @return 消息处理器控制器
         */
        public MessageConsumer startConsumer(MessageHandler handler, int threadCount) {
            MessageConsumer consumer = new MessageConsumer(this, handler, threadCount);
            consumer.start();
            return consumer;
        }
        
        /**
         * 消息处理器
         */
        public interface MessageHandler {
            /**
             * 处理消息
             *
             * @param message 消息内容
             */
            void handle(String message);
        }
        
        /**
         * 消息消费者
         */
        public static class MessageConsumer {
            private final ListQueue queue;
            private final MessageHandler handler;
            private final int threadCount;
            private final AtomicBoolean running = new AtomicBoolean(false);
            private ExecutorService executorService;
            
            public MessageConsumer(ListQueue queue, MessageHandler handler, int threadCount) {
                this.queue = queue;
                this.handler = handler;
                this.threadCount = threadCount;
            }
            
            /**
             * 启动消费者
             */
            public void start() {
                if (running.compareAndSet(false, true)) {
                    executorService = Executors.newFixedThreadPool(threadCount);
                    for (int i = 0; i < threadCount; i++) {
                        executorService.submit(this::consumeMessages);
                    }
                    logger.info("消息消费者已启动: {}, 线程数: {}", queue.queueKey, threadCount);
                }
            }
            
            /**
             * 停止消费者
             */
            public void stop() {
                if (running.compareAndSet(true, false)) {
                    if (executorService != null) {
                        executorService.shutdown();
                    }
                    logger.info("消息消费者已停止: {}", queue.queueKey);
                }
            }
            
            private void consumeMessages() {
                while (running.get()) {
                    try {
                        // 使用1秒超时，以便定期检查running状态
                        String message = queue.receive(1);
                        if (message != null) {
                            try {
                                handler.handle(message);
                            } catch (Exception e) {
                                logger.error("处理消息异常", e);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("消费消息异常", e);
                        // 出现异常时暂停一下，避免频繁重试
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 基于Redis Pub/Sub实现的消息队列
     */
    public static class PubSubQueue {
        private final String channelName;
        
        /**
         * 创建基于Pub/Sub的消息队列
         *
         * @param channelName 频道名称
         */
        public PubSubQueue(String channelName) {
            this.channelName = "redis:mq:pubsub:" + channelName;
        }
        
        /**
         * 发布消息到频道
         *
         * @param message 消息内容
         * @return 接收到消息的客户端数量
         */
        public long publish(String message) {
            try {
                return RedisUtil.execute(jedis -> jedis.publish(channelName, message));
            } catch (Exception e) {
                logger.error("发布消息失败: {}", channelName, e);
                return 0;
            }
        }
        
        /**
         * 发布对象消息到频道（自动序列化为JSON）
         *
         * @param message 消息对象
         * @param <T>     消息类型
         * @return 接收到消息的客户端数量
         */
        public <T> long publishObject(T message) {
            try {
                String jsonMessage = RedisUtil.execute(jedis -> {
                    try {
                        return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(message);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return publish(jsonMessage);
            } catch (Exception e) {
                logger.error("发布对象消息失败: {}", channelName, e);
                return 0;
            }
        }
        
        /**
         * 订阅频道
         *
         * @param listener 消息监听器
         * @return 订阅控制器
         */
        public Subscription subscribe(MessageListener listener) {
            Subscription subscription = new Subscription(this, listener);
            subscription.start();
            return subscription;
        }
        
        /**
         * 消息监听器
         */
        public interface MessageListener {
            /**
             * 接收消息
             *
             * @param channel 频道名称
             * @param message 消息内容
             */
            void onMessage(String channel, String message);
        }
        
        /**
         * 订阅控制器
         */
        public static class Subscription {
            private final PubSubQueue queue;
            private final MessageListener listener;
            private final AtomicBoolean running = new AtomicBoolean(false);
            private Thread subscribeThread;
            private JedisPubSub jedisPubSub;
            
            public Subscription(PubSubQueue queue, MessageListener listener) {
                this.queue = queue;
                this.listener = listener;
            }
            
            /**
             * 开始订阅
             */
            public void start() {
                if (running.compareAndSet(false, true)) {
                    jedisPubSub = new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            try {
                                listener.onMessage(channel, message);
                            } catch (Exception e) {
                                logger.error("处理订阅消息异常", e);
                            }
                        }
                    };
                    
                    subscribeThread = new Thread(() -> {
                        while (running.get()) {
                            try (Jedis jedis = RedisConfig.getJedis()) {
                                logger.info("开始订阅频道: {}", queue.channelName);
                                jedis.subscribe(jedisPubSub, queue.channelName);
                            } catch (Exception e) {
                                if (running.get()) {
                                    logger.error("订阅频道异常，将重试: {}", queue.channelName, e);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ie) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }
                            }
                        }
                    });
                    subscribeThread.setDaemon(true);
                    subscribeThread.start();
                }
            }
            
            /**
             * 停止订阅
             */
            public void stop() {
                if (running.compareAndSet(true, false)) {
                    if (jedisPubSub != null) {
                        jedisPubSub.unsubscribe();
                    }
                    logger.info("已取消订阅频道: {}", queue.channelName);
                }
            }
        }
    }
}