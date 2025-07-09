package org.example.seata.common;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库连接工具类
 */
@Slf4j
public class DatabaseConnection {
    private static final Map<String, DataSource> DATA_SOURCE_MAP = new HashMap<>();

    /**
     * 获取数据源
     *
     * @param dbName 数据库名称
     * @return 数据源
     */
    public static DataSource getDataSource(String dbName) {
        if (DATA_SOURCE_MAP.containsKey(dbName)) {
            return DATA_SOURCE_MAP.get(dbName);
        }

        // 创建Druid数据源
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/" + dbName + "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
        druidDataSource.setInitialSize(5);
        druidDataSource.setMinIdle(5);
        druidDataSource.setMaxActive(20);
        druidDataSource.setMaxWait(60000);

        // 创建Seata代理数据源
        DataSourceProxy dataSourceProxy = new DataSourceProxy(druidDataSource);
        DATA_SOURCE_MAP.put(dbName, dataSourceProxy);
        log.info("创建数据源: {}", dbName);
        return dataSourceProxy;
    }

    /**
     * 获取数据库连接
     *
     * @param dbName 数据库名称
     * @return 数据库连接
     */
    public static Connection getConnection(String dbName) {
        try {
            return getDataSource(dbName).getConnection();
        } catch (SQLException e) {
            log.error("获取数据库连接失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取数据库连接失败", e);
        }
    }

    /**
     * 关闭数据库连接
     *
     * @param connection 数据库连接
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("关闭数据库连接失败: {}", e.getMessage(), e);
            }
        }
    }
}