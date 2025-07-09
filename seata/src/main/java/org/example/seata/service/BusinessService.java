package org.example.seata.service;

/**
 * 业务服务接口
 */
public interface BusinessService {
    /**
     * 创建订单
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param count     数量
     * @return 订单ID
     */
    Long createOrder(String userId, String productId, int count);
    
    /**
     * 创建订单（使用TCC模式）
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param count     数量
     * @return 订单ID
     */
    Long createOrderTCC(String userId, String productId, int count);
    
    /**
     * 创建订单（使用SAGA模式）
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param count     数量
     * @return 订单ID
     */
    Long createOrderSAGA(String userId, String productId, int count);
}