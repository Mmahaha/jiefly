package cn.jiefly.rabbitmq;

import cn.jiefly.rabbitmq.direct.OrderProcessor;
import cn.jiefly.rabbitmq.fanout.LogBroadcaster;
import cn.jiefly.rabbitmq.headers.UserNotifier;
import cn.jiefly.rabbitmq.topic.NewsPublisher;

/**
 * RabbitMQ演示程序，展示各种交换机类型在实际应用场景中的使用
 */
public class RabbitMQDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("===== RabbitMQ 演示程序启动 =====");
        System.out.println("本演示将展示四种交换机类型在实际应用场景中的使用");
        
        // 1. Direct交换机 - 订单处理系统
        System.out.println("\n===== 1. Direct交换机 - 订单处理系统 =====");
        System.out.println("场景：不同类型的订单由不同的处理服务处理，如普通订单、VIP订单、退款订单等");
        OrderProcessor.demo();
        Thread.sleep(1000);
        
        // 2. Fanout交换机 - 日志广播系统
        System.out.println("\n===== 2. Fanout交换机 - 日志广播系统 =====");
        System.out.println("场景：系统日志需要同时发送到多个服务进行处理，如存储、分析、告警等");
        LogBroadcaster.demo();
        Thread.sleep(1000);
        
        // 3. Topic交换机 - 新闻发布系统
        System.out.println("\n===== 3. Topic交换机 - 新闻发布系统 =====");
        System.out.println("场景：新闻按类别和地区发布，用户可以订阅感兴趣的新闻类别和地区");
        NewsPublisher.demo();
        Thread.sleep(1000);
        
        // 4. Headers交换机 - 用户通知系统
        System.out.println("\n===== 4. Headers交换机 - 用户通知系统 =====");
        System.out.println("场景：根据用户属性（如VIP等级、注册时间、活跃度等）发送不同的通知");
        UserNotifier.demo();
        
        System.out.println("\n===== RabbitMQ 演示程序结束 =====");
    }
}