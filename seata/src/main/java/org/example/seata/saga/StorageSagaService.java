package org.example.seata.saga;

/**
 * 库存SAGA服务接口
 */
public interface StorageSagaService {
    /**
     * 扣减库存
     *
     * @param productId 商品ID
     * @param count     数量
     * @return 是否成功
     */
    boolean deduct(String productId, int count);
    
    /**
     * 补偿：恢复库存
     *
     * @param productId 商品ID
     * @param count     数量
     * @return 是否成功
     */
    boolean compensateDeduct(String productId, int count);
}