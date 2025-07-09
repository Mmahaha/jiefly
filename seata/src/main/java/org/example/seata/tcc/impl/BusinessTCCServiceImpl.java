package org.example.seata.tcc.impl;

import io.seata.core.context.RootContext;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.SeataInit;
import org.example.seata.service.BusinessService;
import org.example.seata.tcc.AccountTCCService;
import org.example.seata.tcc.OrderTCCService;
import org.example.seata.tcc.StorageTCCService;

/**
 * TCC模式的业务服务实现类
 */
@Slf4j
public class BusinessTCCServiceImpl implements BusinessService {
    private final OrderTCCService orderTCCService;
    private final StorageTCCService storageTCCService;
    private final AccountTCCService accountTCCService;

    public BusinessTCCServiceImpl() {
        // 初始化Seata客户端
        SeataInit.init();
        
        // 初始化TCC服务
        this.orderTCCService = new OrderTCCServiceImpl();
        this.storageTCCService = new StorageTCCServiceImpl();
        this.accountTCCService = new AccountTCCServiceImpl();
    }

    @Override
    public Long createOrder(String userId, String productId, int count) {
        throw new UnsupportedOperationException("请使用createOrderTCC方法");
    }

    @Override
    public Long createOrderTCC(String userId, String productId, int count) {
        log.info("开始创建订单（TCC模式），用户ID: {}，商品ID: {}，数量: {}", userId, productId, count);
        
        try {
            // 开启全局事务
            GlobalTransactionContext.getCurrentOrCreate().begin(60000, "createOrderTCC");
            log.info("开启全局事务，XID: {}", RootContext.getXID());
            
            // 创建订单-Try阶段
            Long orderId = orderTCCService.prepareCreate(null, userId, productId, count);
            
            // 扣减库存-Try阶段
            storageTCCService.prepareDeduct(null, productId, count);
            
            // 扣减账户余额-Try阶段（订单金额 = 数量 * 10）
            accountTCCService.preparDeduct(null, userId, count * 10);
            
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
    public Long createOrderSAGA(String userId, String productId, int count) {
        throw new UnsupportedOperationException("SAGA模式暂未实现");
    }
}