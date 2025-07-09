package org.example.seata;

import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.saga.impl.BusinessSagaServiceImpl;
import org.example.seata.service.BusinessService;
import org.example.seata.service.impl.BusinessServiceImpl;
import org.example.seata.tcc.impl.BusinessTCCServiceImpl;

/**
 * 分布式事务测试类
 */
@Slf4j
public class TransactionTest {
    public static void main(String[] args) {
        log.info("=== Seata 分布式事务测试 ===");
        log.info("请确保已经启动 Seata Server 并初始化了数据库");
        
        // 测试 AT 模式
        testATMode();
        
        // 测试 TCC 模式
        testTCCMode();
        
        // 测试 SAGA 模式
        testSAGAMode();
    }
    
    /**
     * 测试 AT 模式
     */
    private static void testATMode() {
        log.info("\n=== 测试 AT 模式 ===");
        BusinessService businessService = new BusinessServiceImpl();
        
        // 测试正常场景
        testNormalCase(businessService, "AT");
        
        // 测试库存不足场景
        testInsufficientStorage(businessService, "AT");
        
        // 测试余额不足场景
        testInsufficientBalance(businessService, "AT");
    }
    
    /**
     * 测试 TCC 模式
     */
    private static void testTCCMode() {
        log.info("\n=== 测试 TCC 模式 ===");
        BusinessService businessService = new BusinessTCCServiceImpl();
        
        // 测试正常场景
        testNormalCase(businessService, "TCC");
        
        // 测试库存不足场景
        testInsufficientStorage(businessService, "TCC");
        
        // 测试余额不足场景
        testInsufficientBalance(businessService, "TCC");
    }
    
    /**
     * 测试 SAGA 模式
     */
    private static void testSAGAMode() {
        log.info("\n=== 测试 SAGA 模式 ===");
        BusinessService businessService = new BusinessSagaServiceImpl();
        
        // 测试正常场景
        testNormalCase(businessService, "SAGA");
        
        // 测试库存不足场景
        testInsufficientStorage(businessService, "SAGA");
        
        // 测试余额不足场景
        testInsufficientBalance(businessService, "SAGA");
    }
    
    /**
     * 测试正常场景
     */
    private static void testNormalCase(BusinessService businessService, String mode) {
        log.info("测试{}模式 - 正常场景", mode);
        try {
            Long orderId;
            if ("TCC".equals(mode)) {
                orderId = businessService.createOrderTCC("user1", "product1", 1);
            } else if ("SAGA".equals(mode)) {
                orderId = businessService.createOrderSAGA("user1", "product1", 1);
            } else {
                orderId = businessService.createOrder("user1", "product1", 1);
            }
            log.info("{}模式 - 正常场景 - 订单创建成功，订单ID: {}", mode, orderId);
        } catch (BusinessException e) {
            log.error("{}模式 - 正常场景 - 订单创建失败: {}", mode, e.getMessage());
        } catch (Exception e) {
            log.error("{}模式 - 正常场景 - 发生未知异常: {}", mode, e.getMessage(), e);
        }
    }
    
    /**
     * 测试库存不足场景
     */
    private static void testInsufficientStorage(BusinessService businessService, String mode) {
        log.info("测试{}模式 - 库存不足场景", mode);
        try {
            Long orderId;
            if ("TCC".equals(mode)) {
                orderId = businessService.createOrderTCC("user1", "product1", 1000);
            } else if ("SAGA".equals(mode)) {
                orderId = businessService.createOrderSAGA("user1", "product1", 1000);
            } else {
                orderId = businessService.createOrder("user1", "product1", 1000);
            }
            log.info("{}模式 - 库存不足场景 - 订单创建成功，订单ID: {}", mode, orderId);
        } catch (BusinessException e) {
            log.info("{}模式 - 库存不足场景 - 订单创建失败（符合预期）: {}", mode, e.getMessage());
        } catch (Exception e) {
            log.error("{}模式 - 库存不足场景 - 发生未知异常: {}", mode, e.getMessage(), e);
        }
    }
    
    /**
     * 测试余额不足场景
     */
    private static void testInsufficientBalance(BusinessService businessService, String mode) {
        log.info("测试{}模式 - 余额不足场景", mode);
        try {
            Long orderId;
            if ("TCC".equals(mode)) {
                orderId = businessService.createOrderTCC("user1", "product1", 200);
            } else if ("SAGA".equals(mode)) {
                orderId = businessService.createOrderSAGA("user1", "product1", 200);
            } else {
                orderId = businessService.createOrder("user1", "product1", 200);
            }
            log.info("{}模式 - 余额不足场景 - 订单创建成功，订单ID: {}", mode, orderId);
        } catch (BusinessException e) {
            log.info("{}模式 - 余额不足场景 - 订单创建失败（符合预期）: {}", mode, e.getMessage());
        } catch (Exception e) {
            log.error("{}模式 - 余额不足场景 - 发生未知异常: {}", mode, e.getMessage(), e);
        }
    }
}