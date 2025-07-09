package org.example.seata.service;

/**
 * 库存服务接口
 */
public interface StorageService {
    /**
     * 扣减库存
     *
     * @param productId 商品ID
     * @param count     数量
     */
    void deduct(String productId, int count);
    
    /**
     * 获取库存数量
     *
     * @param productId 商品ID
     * @return 库存数量
     */
    int getCount(String productId);
}