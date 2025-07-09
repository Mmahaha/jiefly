package org.example.seata.tcc;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * 订单TCC服务接口
 */
@LocalTCC
public interface OrderTCCService {
    /**
     * 创建订单-Try阶段
     *
     * @param actionContext 事务上下文
     * @param userId        用户ID
     * @param productId     商品ID
     * @param count         数量
     * @return 订单ID
     */
    @TwoPhaseBusinessAction(name = "orderTCCService", commitMethod = "commit", rollbackMethod = "rollback")
    Long prepareCreate(BusinessActionContext actionContext,
                       @BusinessActionContextParameter(paramName = "userId") String userId,
                       @BusinessActionContextParameter(paramName = "productId") String productId,
                       @BusinessActionContextParameter(paramName = "count") int count);

    /**
     * 创建订单-Confirm阶段
     *
     * @param actionContext 事务上下文
     * @return 是否成功
     */
    boolean commit(BusinessActionContext actionContext);

    /**
     * 创建订单-Cancel阶段
     *
     * @param actionContext 事务上下文
     * @return 是否成功
     */
    boolean rollback(BusinessActionContext actionContext);
}