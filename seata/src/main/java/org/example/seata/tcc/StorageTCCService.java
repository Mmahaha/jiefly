package org.example.seata.tcc;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * 库存TCC服务接口
 */
@LocalTCC
public interface StorageTCCService {
    /**
     * 扣减库存-Try阶段
     *
     * @param actionContext 事务上下文
     * @param productId     商品ID
     * @param count         数量
     * @return 是否成功
     */
    @TwoPhaseBusinessAction(name = "storageTCCService", commitMethod = "commit", rollbackMethod = "rollback")
    boolean prepareDeduct(BusinessActionContext actionContext,
                          @BusinessActionContextParameter(paramName = "productId") String productId,
                          @BusinessActionContextParameter(paramName = "count") int count);

    /**
     * 扣减库存-Confirm阶段
     *
     * @param actionContext 事务上下文
     * @return 是否成功
     */
    boolean commit(BusinessActionContext actionContext);

    /**
     * 扣减库存-Cancel阶段
     *
     * @param actionContext 事务上下文
     * @return 是否成功
     */
    boolean rollback(BusinessActionContext actionContext);
}