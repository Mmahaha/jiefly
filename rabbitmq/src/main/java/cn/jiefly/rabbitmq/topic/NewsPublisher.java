package cn.jiefly.rabbitmq.topic;

import cn.jiefly.rabbitmq.util.RabbitMQUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Topic交换机示例 - 新闻发布系统
 * 场景：新闻按类别和地区发布，用户可以订阅感兴趣的新闻类别和地区
 */
public class NewsPublisher {
    private static final String EXCHANGE_NAME = "news_exchange";
    
    // 队列名称
    private static final String TECH_QUEUE = "tech_news_queue";
    private static final String SPORTS_QUEUE = "sports_news_queue";
    private static final String BEIJING_QUEUE = "beijing_news_queue";
    private static final String ALL_NEWS_QUEUE = "all_news_queue";
    
    public static void demo() throws Exception {
        // 启动新闻订阅者
        startNewsSubscribers();
        
        // 发布新闻
        publishNews();
    }
    
    /**
     * 启动新闻订阅者
     */
    private static void startNewsSubscribers() {
        // 科技新闻订阅者 - 订阅所有科技新闻
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
                // 声明队列
                channel.queueDeclare(TECH_QUEUE, false, false, false, null);
                // 绑定队列到交换机，使用通配符 #
                channel.queueBind(TECH_QUEUE, EXCHANGE_NAME, "tech.#");
                
                System.out.println("科技新闻订阅者已启动，等待接收科技新闻...");
                
                // 创建消费者
                channel.basicConsume(TECH_QUEUE, true, (consumerTag, message) -> {
                    String newsContent = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("科技新闻订阅者收到新闻: " + newsContent);
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 体育新闻订阅者 - 订阅所有体育新闻
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
                // 声明队列
                channel.queueDeclare(SPORTS_QUEUE, false, false, false, null);
                // 绑定队列到交换机，使用通配符 #
                channel.queueBind(SPORTS_QUEUE, EXCHANGE_NAME, "sports.#");
                
                System.out.println("体育新闻订阅者已启动，等待接收体育新闻...");
                
                // 创建消费者
                channel.basicConsume(SPORTS_QUEUE, true, (consumerTag, message) -> {
                    String newsContent = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("体育新闻订阅者收到新闻: " + newsContent);
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 北京新闻订阅者 - 订阅所有北京地区的新闻
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
                // 声明队列
                channel.queueDeclare(BEIJING_QUEUE, false, false, false, null);
                // 绑定队列到交换机，使用通配符 *
                channel.queueBind(BEIJING_QUEUE, EXCHANGE_NAME, "*.beijing");
                
                System.out.println("北京新闻订阅者已启动，等待接收北京地区新闻...");
                
                // 创建消费者
                channel.basicConsume(BEIJING_QUEUE, true, (consumerTag, message) -> {
                    String newsContent = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("北京新闻订阅者收到新闻: " + newsContent);
                }, consumerTag -> {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        // 全部新闻订阅者 - 订阅所有新闻
        new Thread(() -> {
            try {
                Channel channel = RabbitMQUtil.getChannel();
                // 声明交换机
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
                // 声明队列
                channel.queueDeclare(ALL_NEWS_QUEUE, false, false, false, null);
                // 绑定队列到交换机，使用通配符 #
                channel.queueBind(ALL_NEWS_QUEUE, EXCHANGE_NAME, "#");
                
                System.out.println("全部新闻订阅者已启动，等待接收所有新闻...");
                
                // 创建消费者
                channel.basicConsume(ALL_NEWS_QUEUE, true, (consumerTag, message) -> {
                    String newsContent = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("全部新闻订阅者收到新闻: " + newsContent);
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
     * 发布新闻
     */
    private static void publishNews() throws Exception {
        Channel channel = RabbitMQUtil.getChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        
        // 发布科技新闻 - 北京地区
        String techBeijingNews = "北京举办2023年全球科技创新峰会，吸引众多科技巨头参与";
        channel.basicPublish(EXCHANGE_NAME, "tech.beijing", null, techBeijingNews.getBytes());
        System.out.println("已发布科技.北京新闻: " + techBeijingNews);
        
        // 发布科技新闻 - 上海地区
        String techShanghaiNews = "上海人工智能研究院发布最新研究成果，取得重大突破";
        channel.basicPublish(EXCHANGE_NAME, "tech.shanghai", null, techShanghaiNews.getBytes());
        System.out.println("已发布科技.上海新闻: " + techShanghaiNews);
        
        // 发布体育新闻 - 北京地区
        String sportsBeijingNews = "北京冬奥会场馆将举办新赛季冰雪项目国际赛事";
        channel.basicPublish(EXCHANGE_NAME, "sports.beijing", null, sportsBeijingNews.getBytes());
        System.out.println("已发布体育.北京新闻: " + sportsBeijingNews);
        
        // 发布体育新闻 - 广州地区
        String sportsGuangzhouNews = "广州恒大足球俱乐部宣布新赛季目标和新引援计划";
        channel.basicPublish(EXCHANGE_NAME, "sports.guangzhou", null, sportsGuangzhouNews.getBytes());
        System.out.println("已发布体育.广州新闻: " + sportsGuangzhouNews);
        
        // 关闭资源
        RabbitMQUtil.closeConnectionAndChannel(channel, channel.getConnection());
    }
}