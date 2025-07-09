package org.example.seata.tcc;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * 账户TCC服务接口
 */
@LocalTCC
public interface AccountTCCService {
    /**
     * 扣减账户余额-Try阶段
     *
     * @param actionContext 事务上下文
     * @param userId        用户ID
     * @param money         金额
     * @return 是否成功
     */
    @TwoPhaseBusinessAction(name = "accountTCCService", commitMethod = "commit", rollbackMethod = "rollback")
    boolean preparDeduct(BusinessActionContext actionContext,
                         @BusinessActionContextParameter(paramName = "userId") String userId,
                         @BusinessActionContextParameter(paramName = "money") int money);

    /**
     * 扣减账户余额-Confirm阶段
     *
     * @param actionContext 事务上下文
     * @return 是否成功
     */
    boolean commit(BusinessActionContext actionContext);

    /**
     * 扣减账户余额-Cancel阶段
     *
     * @param actionContext 事务上下文
     * @return 是否成功
     */
    boolean rollback(BusinessActionContext actionContext);
}