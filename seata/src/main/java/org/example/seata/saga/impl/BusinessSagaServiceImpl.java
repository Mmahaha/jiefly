package org.example.seata.saga.impl;

import io.seata.core.context.RootContext;
import io.seata.saga.engine.StateMachineEngine;
import io.seata.saga.statelang.domain.StateMachineInstance;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.SeataInit;
import org.example.seata.saga.AccountSagaService;
import org.example.seata.saga.OrderSagaService;
import org.example.seata.saga.StorageSagaService;
import org.example.seata.service.BusinessService;

import java.util.HashMap;
import java.util.Map;

/**
 * SAGA模式的业务服务实现类
 */
@Slf4j
public class BusinessSagaServiceImpl implements BusinessService {
    private final OrderSagaService orderSagaService;
    private final StorageSagaService storageSagaService;
    private final AccountSagaService accountSagaService;

    public BusinessSagaServiceImpl() {
        // 初始化Seata客户端
        SeataInit.init();
        
        // 初始化SAGA服务
        this.orderSagaService = new OrderSagaServiceImpl();
        this.storageSagaService = new StorageSagaServiceImpl();
        this.accountSagaService = new AccountSagaServiceImpl();
    }

    @Override
    public Long createOrder(String userId, String productId, int count) {
        throw new UnsupportedOperationException("请使用createOrderSAGA方法");
    }

    @Override
    public Long createOrderTCC(String userId, String productId, int count) {
        throw new UnsupportedOperationException("请使用createOrderSAGA方法");
    }

    @Override
    public Long createOrderSAGA(String userId, String productId, int count) {
        log.info("开始创建订单（SAGA模式），用户ID: {}，商品ID: {}，数量: {}", userId, productId, count);
        Long orderId = null;
        
        try {
            // 开启全局事务
            GlobalTransactionContext.getCurrentOrCreate().begin(60000, "createOrderSAGA");
            log.info("开启全局事务，XID: {}", RootContext.getXID());
            
            // 1. 创建订单
            orderId = orderSagaService.create(userId, productId, count);
            log.info("创建订单成功，订单ID: {}", orderId);
            
            // 2. 扣减库存
            boolean storageResult = storageSagaService.deduct(productId, count);
            if (!storageResult) {
                throw new BusinessException("扣减库存失败");
            }
            log.info("扣减库存成功");
            
            // 3. 扣减账户余额
            boolean accountResult = accountSagaService.deduct(userId, count * 10);
            if (!accountResult) {
                throw new BusinessException("扣减账户余额失败");
            }
            log.info("扣减账户余额成功");
            
            // 4. 更新订单状态
            boolean updateResult = orderSagaService.updateStatus(orderId, 1);
            if (!updateResult) {
                throw new BusinessException("更新订单状态失败");
            }
            log.info("更新订单状态成功");
            
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
                
                // 手动补偿
                if (orderId != null) {
                    orderSagaService.compensateCreate(orderId);
                }
                storageSagaService.compensateDeduct(productId, count);
                accountSagaService.compensateDeduct(userId, count * 10);
            } catch (Exception ex) {
                log.error("回滚全局事务失败: {}", ex.getMessage(), ex);
            }
            throw new BusinessException("创建订单失败: " + e.getMessage(), e);
        }
    }
}