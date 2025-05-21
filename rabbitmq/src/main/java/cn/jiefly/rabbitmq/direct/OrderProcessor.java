package cn.jiefly.rabbitmq.direct;

import cn.jiefly.rabbitmq.util.RabbitMQUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Direct交换机示例 - 订单处理系统
 * 场景：不同类型的订单由不同的处理服务处理，如普通订单、VIP订单、退款订单等
 */
public class OrderProcessor {
    private static final String EXCHANGE_NAME = "order_exchange";
    private static final String NORMAL_ORDER_QUEUE = "normal_order_queue";
    private static final String VIP_ORDER_QUEUE = "vip_order_queue";
    private static final String REFUND_ORDER_QUEUE = "refund_order_queue";
    
    // 路由键
    private static final String NORMAL_ORDER_KEY = "order.normal";
    private static final String VIP_ORDER_KEY = "order.vip";
    private static final String REFUND_ORDER_KEY = "order.refund";
    
    public static void demo() throws Exception {
        // 启动消费者
        startConsumers();
        
        // 发送订单消息
        sendOrders();
    }
    
    /**
     * 启动订单处理消费者
     */
    private static void startConsumers() {
        // 启动普通订单处理服务
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                // 声明队列
                channel.queueDeclare(NORMAL_ORDER_QUEUE, false, false, false, null);
                // 绑定队列到交换机
                channel.queueBind(NORMAL_ORDER_QUEUE, EXCHANGE_NAME, NORMAL_ORDER_KEY);
                System.out.println("普通订单处理服务已启动，等待处理普通订单...");
                
                // 创建消费者
                channel.basicConsume(NORMAL_ORDER_QUEUE, true, (consumerTag, message) -> {
                    String orderInfo = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("普通订单处理服务接收到订单: " + orderInfo);
                    System.out.println("普通订单处理中...");
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 启动VIP订单处理服务
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                // 声明队列
                channel.queueDeclare(VIP_ORDER_QUEUE, false, false, false, null);
                // 绑定队列到交换机
                channel.queueBind(VIP_ORDER_QUEUE, EXCHANGE_NAME, VIP_ORDER_KEY);
                
                System.out.println("VIP订单处理服务已启动，等待处理VIP订单...");
                
                // 创建消费者
                channel.basicConsume(VIP_ORDER_QUEUE, true, (consumerTag, message) -> {
                    String orderInfo = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("VIP订单处理服务接收到订单: " + orderInfo);
                    System.out.println("VIP订单加急处理中...");
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 启动退款订单处理服务
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                // 声明队列
                channel.queueDeclare(REFUND_ORDER_QUEUE, false, false, false, null);
                // 绑定队列到交换机
                channel.queueBind(REFUND_ORDER_QUEUE, EXCHANGE_NAME, REFUND_ORDER_KEY);
                
                System.out.println("退款订单处理服务已启动，等待处理退款订单...");
                
                // 创建消费者
                channel.basicConsume(REFUND_ORDER_QUEUE, true, (consumerTag, message) -> {
                    String orderInfo = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("退款订单处理服务接收到订单: " + orderInfo);
                    System.out.println("退款订单处理中...");
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
     * 发送订单消息
     */
    private static void sendOrders() throws Exception {
        Channel channel = RabbitMQUtil.getChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        
        // 发送普通订单
        String normalOrderInfo = "订单号: 10001, 用户: 张三, 商品: 手机, 金额: 3999元";
        channel.basicPublish(EXCHANGE_NAME, NORMAL_ORDER_KEY, null, normalOrderInfo.getBytes());
        System.out.println("已发送普通订单: " + normalOrderInfo);
        
        // 发送VIP订单
        String vipOrderInfo = "订单号: 10002, 用户: 李四(VIP客户), 商品: 笔记本电脑, 金额: 8999元";
        channel.basicPublish(EXCHANGE_NAME, VIP_ORDER_KEY, null, vipOrderInfo.getBytes());
        System.out.println("已发送VIP订单: " + vipOrderInfo);
        
        // 发送退款订单
        String refundOrderInfo = "订单号: 10003, 用户: 王五, 商品: 耳机, 金额: 299元, 退款原因: 商品损坏";
        channel.basicPublish(EXCHANGE_NAME, REFUND_ORDER_KEY, null, refundOrderInfo.getBytes());
        System.out.println("已发送退款订单: " + refundOrderInfo);
        
        // 关闭资源
        RabbitMQUtil.closeConnectionAndChannel(channel, channel.getConnection());
    }
}