package org.example.seata.service.impl;

import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransactionContext;
import io.seata.tm.api.TransactionalExecutor;
import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.SeataInit;
import org.example.seata.service.AccountService;
import org.example.seata.service.BusinessService;
import org.example.seata.service.OrderService;
import org.example.seata.service.StorageService;

/**
 * 业务服务实现类
 */
@Slf4j
public class BusinessServiceImpl implements BusinessService {
    private final OrderService orderService;
    private final StorageService storageService;
    private final AccountService accountService;

    public BusinessServiceImpl() {
        // 初始化Seata客户端
        SeataInit.init();
        
        // 初始化服务
        this.orderService = new OrderServiceImpl();
        this.storageService = new StorageServiceImpl();
        this.accountService = new AccountServiceImpl();
    }

    @Override
    public Long createOrder(String userId, String productId, int count) {
        log.info("开始创建订单，用户ID: {}，商品ID: {}，数量: {}", userId, productId, count);
        
        try {
            // 开启全局事务
            GlobalTransactionContext.getCurrentOrCreate().begin(60000, "createOrder");
            log.info("开启全局事务，XID: {}", RootContext.getXID());
            
            // 创建订单
            Long orderId = orderService.create(userId, productId, count);
            
            // 扣减库存
            storageService.deduct(productId, count);
            
            // 扣减账户余额（订单金额 = 数量 * 10）
            accountService.deduct(userId, count * 10);
            
            // 更新订单状态
            orderService.updateStatus(orderId, 1);
            
            // 提交事务
            GlobalTransactionContext.getCurrentOrCreate().commit();
            log.info("提交全局事务，XID: {}", RootContext.getXID());
            
            return orderId;
        } catch (Exception e) {
            log.error("创建订单失败: {}", e.getMessage(), e);
            try {
                // 回滚事务
                GlobalTransactionContext.getCurrentOrCreate().rollback();
                log.info("回滚全局事务，XID: {}", RootContext.getXID());
            } catch (Exception ex) {
                log.error("回滚全局事务失败: {}", ex.getMessage(), ex);
            }
            throw new BusinessException("创建订单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Long createOrderTCC(String userId, String productId, int count) {
        // TCC模式实现将在TCC相关类中实现
        throw new UnsupportedOperationException("TCC模式暂未实现");
    }

    @Override
    public Long createOrderSAGA(String userId, String productId, int count) {
        // SAGA模式实现将在SAGA相关类中实现
        throw new UnsupportedOperationException("SAGA模式暂未实现");
    }
}