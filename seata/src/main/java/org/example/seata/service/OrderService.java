package org.example.seata.service;

import org.example.seata.entity.Order;

/**
 * 订单服务接口
 */
public interface OrderService {
    /**
     * 创建订单
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param count     数量
     * @return 订单ID
     */
    Long create(String userId, String productId, int count);
    
    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param status  状态
     */
    void updateStatus(Long orderId, int status);
    
    /**
     * 获取订单
     *
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order getById(Long orderId);
}