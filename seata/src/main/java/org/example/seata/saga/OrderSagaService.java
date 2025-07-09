package org.example.seata.saga;

/**
 * 订单SAGA服务接口
 */
public interface OrderSagaService {
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
     * 补偿：删除订单
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean compensateCreate(Long orderId);
    
    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param status  状态
     * @return 是否成功
     */
    boolean updateStatus(Long orderId, int status);
}