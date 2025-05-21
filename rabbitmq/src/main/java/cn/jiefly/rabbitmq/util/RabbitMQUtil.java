package cn.jiefly.rabbitmq.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ工具类，用于创建连接和通道
 */
public class RabbitMQUtil {
    private static final String HOST = "192.168.3.5";
    private static final int PORT = 5672;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String VIRTUAL_HOST = "/";
    private static Connection connection;

    /**
     * 获取RabbitMQ连接
     *
     * @return Connection对象
     */
    public synchronized static Connection getConnection() throws IOException, TimeoutException {
        if (connection == null) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setPort(PORT);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);
            factory.setVirtualHost(VIRTUAL_HOST);
            connection = factory.newConnection();
        }
        return connection;
    }

    /**
     * 获取Channel通道
     *
     * @return Channel对象
     */
    public static Channel getChannel() throws IOException, TimeoutException {
        return getConnection().createChannel();
    }

    /**
     * 关闭通道和连接
     *
     * @param channel    通道
     * @param connection 连接
     */
    public static void closeConnectionAndChannel(Channel channel, Connection connection) {
        try {
            if (channel != null) {
                channel.close();
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}