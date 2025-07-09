package org.example.seata.saga;

/**
 * 账户SAGA服务接口
 */
public interface AccountSagaService {
    /**
     * 扣减账户余额
     *
     * @param userId 用户ID
     * @param money  金额
     * @return 是否成功
     */
    boolean deduct(String userId, int money);
    
    /**
     * 补偿：恢复账户余额
     *
     * @param userId 用户ID
     * @param money  金额
     * @return 是否成功
     */
    boolean compensateDeduct(String userId, int money);
}