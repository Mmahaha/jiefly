package cn.jiefly.rabbitmq.fanout;

import cn.jiefly.rabbitmq.util.RabbitMQUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Fanout交换机示例 - 日志广播系统
 * 场景：系统日志需要同时发送到多个服务进行处理，如存储、分析、告警等
 */
public class LogBroadcaster {
    private static final String EXCHANGE_NAME = "logs_exchange";
    private static final String STORAGE_QUEUE = "logs_storage_queue";
    private static final String ANALYSIS_QUEUE = "logs_analysis_queue";
    private static final String ALERT_QUEUE = "logs_alert_queue";
    
    public static void demo() throws Exception {
        // 启动日志处理消费者
        startLogConsumers();
        
        // 发送日志消息
        sendLogs();
    }
    
    /**
     * 启动日志处理消费者
     */
    private static void startLogConsumers() {
        // 启动日志存储服务
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
                // 声明队列
                channel.queueDeclare(STORAGE_QUEUE, false, false, false, null);
                // 绑定队列到交换机
                channel.queueBind(STORAGE_QUEUE, EXCHANGE_NAME, "");
                
                System.out.println("日志存储服务已启动，等待接收日志...");
                // 创建消费者
                channel.basicConsume(STORAGE_QUEUE, true, (consumerTag, message) -> {
                    String logInfo = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("日志存储服务接收到日志: " + logInfo);
                    System.out.println("正在将日志保存到数据库...");
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 启动日志分析服务
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
                // 声明队列
                channel.queueDeclare(ANALYSIS_QUEUE, false, false, false, null);
                // 绑定队列到交换机
                channel.queueBind(ANALYSIS_QUEUE, EXCHANGE_NAME, "");
                
                System.out.println("日志分析服务已启动，等待接收日志...");
                
                // 创建消费者
                channel.basicConsume(ANALYSIS_QUEUE, true, (consumerTag, message) -> {
                    String logInfo = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("日志分析服务接收到日志: " + logInfo);
                    System.out.println("正在分析日志内容...");
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 启动日志告警服务
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
                // 声明队列
                channel.queueDeclare(ALERT_QUEUE, false, false, false, null);
                // 绑定队列到交换机
                channel.queueBind(ALERT_QUEUE, EXCHANGE_NAME, "");
                
                System.out.println("日志告警服务已启动，等待接收日志...");
                
                // 创建消费者
                channel.basicConsume(ALERT_QUEUE, true, (consumerTag, message) -> {
                    String logInfo = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("日志告警服务接收到日志: " + logInfo);
                    if (logInfo.contains("ERROR")) {
                        System.out.println("发现错误日志，正在发送告警通知...");
                    } else {
                        System.out.println("日志级别正常，无需告警");
                    }
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 等待消费者启动
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 发送日志消息
     */
    private static void sendLogs() throws Exception {
        Channel channel = RabbitMQUtil.getChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        
        // 发送普通日志
        String infoLog = "[INFO] 2023-05-15 10:15:30 - 用户登录成功，用户ID: 12345";
        channel.basicPublish(EXCHANGE_NAME, "", null, infoLog.getBytes());
        System.out.println("已发送日志: " + infoLog);
        
        // 发送警告日志
        String warnLog = "[WARN] 2023-05-15 10:16:45 - 用户尝试访问未授权资源，用户ID: 12345";
        channel.basicPublish(EXCHANGE_NAME, "", null, warnLog.getBytes());
        System.out.println("已发送日志: " + warnLog);
        
        // 发送错误日志
        String errorLog = "[ERROR] 2023-05-15 10:18:20 - 数据库连接失败，服务不可用";
        channel.basicPublish(EXCHANGE_NAME, "", null, errorLog.getBytes());
        System.out.println("已发送日志: " + errorLog);
        
        // 关闭资源
        RabbitMQUtil.closeConnectionAndChannel(channel, channel.getConnection());
    }
}