package org.example.seata.common;

import io.seata.common.util.StringUtils;
import io.seata.config.Configuration;
import io.seata.config.ConfigurationFactory;
import io.seata.core.context.RootContext;
import io.seata.core.rpc.netty.RmNettyRemotingClient;
import io.seata.core.rpc.netty.TmNettyRemotingClient;
import io.seata.rm.RMClient;
import io.seata.tm.TMClient;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Seata初始化工具类
 */
@Slf4j
public class SeataInit {
    private static final Configuration CONFIG = ConfigurationFactory.getInstance();
    private static final String APPLICATION_ID = "seata-demo";
    private static final String TX_SERVICE_GROUP = "my_test_tx_group";
    private static volatile boolean initialized = false;

    /**
     * 初始化Seata客户端
     */
    public static void init() {
        if (initialized) {
            return;
        }
        synchronized (SeataInit.class) {
            if (initialized) {
                return;
            }
            try {
                // 初始化事务管理器客户端
                TMClient.init(APPLICATION_ID, TX_SERVICE_GROUP);
                log.info("Transaction Manager Client initialized");
                
                // 初始化资源管理器客户端
                RMClient.init(APPLICATION_ID, TX_SERVICE_GROUP);
                log.info("Resource Manager Client initialized");
                
                initialized = true;
                log.info("Seata initialized successfully");
            } catch (Exception e) {
                log.error("Seata initialization error", e);
                throw new RuntimeException("Seata initialization error", e);
            }
        }
    }

    /**
     * 获取当前事务XID
     *
     * @return 当前事务XID
     */
    public static String getXID() {
        return RootContext.getXID();
    }

    /**
     * 判断是否在全局事务中
     *
     * @return 是否在全局事务中
     */
    public static boolean isInGlobalTransaction() {
        return StringUtils.isNotBlank(RootContext.getXID());
    }

    /**
     * 清理Seata客户端资源
     */
    public static void destroy() {
        if (!initialized) {
            return;
        }
        try {
            // 关闭TM客户端
            TmNettyRemotingClient.getInstance().destroy();
            // 关闭RM客户端
            RmNettyRemotingClient.getInstance().destroy();
            initialized = false;
            log.info("Seata client destroyed");
        } catch (Exception e) {
            log.error("Seata client destroy error", e);
        }
    }
}