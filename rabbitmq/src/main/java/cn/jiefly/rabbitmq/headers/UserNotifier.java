package cn.jiefly.rabbitmq.headers;

import cn.jiefly.rabbitmq.util.RabbitMQUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Headers交换机示例 - 用户通知系统
 * 场景：根据用户属性（如VIP等级、注册时间、活跃度等）发送不同的通知
 */
public class UserNotifier {
    private static final String EXCHANGE_NAME = "user_notification_exchange";
    
    // 队列名称
    private static final String VIP_QUEUE = "vip_notification_queue";
    private static final String NEW_USER_QUEUE = "new_user_notification_queue";
    private static final String ACTIVE_USER_QUEUE = "active_user_notification_queue";
    private static final String INACTIVE_USER_QUEUE = "inactive_user_notification_queue";
    
    public static void demo() throws Exception {
        // 启动通知接收者
        startNotificationReceivers();
        
        // 发送用户通知
        sendUserNotifications();
    }
    
    /**
     * 启动通知接收者
     */
    private static void startNotificationReceivers() {
        // VIP用户通知接收者
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);
                // 声明队列
                channel.queueDeclare(VIP_QUEUE, false, false, false, null);
                
                // 设置绑定参数 - 匹配VIP用户
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("x-match", "any"); // 任意一个键匹配即可
                arguments.put("vip_level", "gold");
                arguments.put("vip_level", "platinum");
                
                // 绑定队列到交换机
                channel.queueBind(VIP_QUEUE, EXCHANGE_NAME, "", arguments);
                
                System.out.println("VIP用户通知接收者已启动，等待接收VIP用户通知...");
                
                // 创建消费者
                channel.basicConsume(VIP_QUEUE, true, (consumerTag, message) -> {
                    String notificationContent = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("VIP用户通知接收者收到通知: " + notificationContent);
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 新用户通知接收者
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);
                // 声明队列
                channel.queueDeclare(NEW_USER_QUEUE, false, false, false, null);
                
                // 设置绑定参数 - 匹配新用户
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("x-match", "all"); // 所有键都必须匹配
                arguments.put("registration_days", "<30"); // 注册时间少于30天
                
                // 绑定队列到交换机
                channel.queueBind(NEW_USER_QUEUE, EXCHANGE_NAME, "", arguments);
                
                System.out.println("新用户通知接收者已启动，等待接收新用户通知...");
                
                // 创建消费者
                channel.basicConsume(NEW_USER_QUEUE, true, (consumerTag, message) -> {
                    String notificationContent = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("新用户通知接收者收到通知: " + notificationContent);
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 活跃用户通知接收者
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);
                // 声明队列
                channel.queueDeclare(ACTIVE_USER_QUEUE, false, false, false, null);
                
                // 设置绑定参数 - 匹配活跃用户
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("x-match", "all"); // 所有键都必须匹配
                arguments.put("activity_level", "high"); // 活跃度高
                
                // 绑定队列到交换机
                channel.queueBind(ACTIVE_USER_QUEUE, EXCHANGE_NAME, "", arguments);
                
                System.out.println("活跃用户通知接收者已启动，等待接收活跃用户通知...");
                
                // 创建消费者
                channel.basicConsume(ACTIVE_USER_QUEUE, true, (consumerTag, message) -> {
                    String notificationContent = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("活跃用户通知接收者收到通知: " + notificationContent);
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 不活跃用户通知接收者
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);
                // 声明队列
                channel.queueDeclare(INACTIVE_USER_QUEUE, false, false, false, null);
                
                // 设置绑定参数 - 匹配不活跃用户
                Map<String, Object> arguments = new HashMap<>();
                arguments.put("x-match", "all"); // 所有键都必须匹配
                arguments.put("activity_level", "low"); // 活跃度低
                arguments.put("last_login_days", ">30"); // 最后登录时间超过30天
                
                // 绑定队列到交换机
                channel.queueBind(INACTIVE_USER_QUEUE, EXCHANGE_NAME, "", arguments);
                
                System.out.println("不活跃用户通知接收者已启动，等待接收不活跃用户通知...");
                
                // 创建消费者
                channel.basicConsume(INACTIVE_USER_QUEUE, true, (consumerTag, message) -> {
                    String notificationContent = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("不活跃用户通知接收者收到通知: " + notificationContent);
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
     * 发送用户通知
     */
    private static void sendUserNotifications() throws Exception {
        Channel channel = RabbitMQUtil.getChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);
        
        // 发送VIP用户通知
        String vipNotification = "尊敬的VIP用户，您有专属优惠券已到账，请查收！";
        Map<String, Object> vipHeaders = new HashMap<>();
        vipHeaders.put("vip_level", "gold");
        
        AMQP.BasicProperties vipProperties = new AMQP.BasicProperties.Builder()
                .headers(vipHeaders)
                .build();
        
        channel.basicPublish(EXCHANGE_NAME, "", vipProperties, vipNotification.getBytes());
        System.out.println("已发送VIP用户通知: " + vipNotification);
        
        // 发送新用户通知
        String newUserNotification = "欢迎加入我们！新用户专享首单立减50元，活动详情请查看站内信";
        Map<String, Object> newUserHeaders = new HashMap<>();
        newUserHeaders.put("registration_days", "<30");
        
        AMQP.BasicProperties newUserProperties = new AMQP.BasicProperties.Builder()
                .headers(newUserHeaders)
                .build();
        
        channel.basicPublish(EXCHANGE_NAME, "", newUserProperties, newUserNotification.getBytes());
        System.out.println("已发送新用户通知: " + newUserNotification);
        
        // 发送活跃用户通知
        String activeUserNotification = "感谢您的活跃参与！您已获得本月积分奖励，可兑换多种好礼";
        Map<String, Object> activeUserHeaders = new HashMap<>();
        activeUserHeaders.put("activity_level", "high");
        
        AMQP.BasicProperties activeUserProperties = new AMQP.BasicProperties.Builder()
                .headers(activeUserHeaders)
                .build();
        
        channel.basicPublish(EXCHANGE_NAME, "", activeUserProperties, activeUserNotification.getBytes());
        System.out.println("已发送活跃用户通知: " + activeUserNotification);
        
        // 发送不活跃用户通知
        String inactiveUserNotification = "好久不见！登录即送100积分，更有专属优惠等你来领取";
        Map<String, Object> inactiveUserHeaders = new HashMap<>();
        inactiveUserHeaders.put("activity_level", "low");
        inactiveUserHeaders.put("last_login_days", ">30");
        
        AMQP.BasicProperties inactiveUserProperties = new AMQP.BasicProperties.Builder()
                .headers(inactiveUserHeaders)
                .build();
        
        channel.basicPublish(EXCHANGE_NAME, "", inactiveUserProperties, inactiveUserNotification.getBytes());
        System.out.println("已发送不活跃用户通知: " + inactiveUserNotification);
        
        // 关闭资源
        RabbitMQUtil.closeConnectionAndChannel(channel, channel.getConnection());
    }
}