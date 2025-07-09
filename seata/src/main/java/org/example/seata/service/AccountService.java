package org.example.seata.service;

/**
 * 账户服务接口
 */
public interface AccountService {
    /**
     * 扣减账户余额
     *
     * @param userId 用户ID
     * @param money  金额
     */
    void deduct(String userId, int money);
    
    /**
     * 获取账户余额
     *
     * @param userId 用户ID
     * @return 账户余额
     */
    int getBalance(String userId);
}