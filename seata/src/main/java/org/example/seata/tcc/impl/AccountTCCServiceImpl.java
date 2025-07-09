package org.example.seata.tcc.impl;

import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.DatabaseConnection;
import org.example.seata.service.AccountService;
import org.example.seata.service.impl.AccountServiceImpl;
import org.example.seata.tcc.AccountTCCService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 账户TCC服务实现类
 */
@Slf4j
public class AccountTCCServiceImpl implements AccountTCCService {
    private static final String DB_NAME = "seata_account";
    private static final Map<String, Boolean> FREEZE_RECORDS = new ConcurrentHashMap<>();
    private final AccountService accountService;

    public AccountTCCServiceImpl() {
        this.accountService = new AccountServiceImpl();
    }

    @Override
    public boolean preparDeduct(BusinessActionContext actionContext, String userId, int money) {
        log.info("账户服务TCC-Try阶段开始，XID: {}, userId: {}, money: {}", actionContext.getXid(), userId, money);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 检查余额是否足够
            int balance = accountService.getBalance(userId);
            if (balance < money) {
                throw new BusinessException("账户余额不足");
            }
            
            // 冻结金额（预留资源）
            String sql = "UPDATE account SET money = money - ? WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, money);
            ps.setString(2, userId);
            int result = ps.executeUpdate();
            
            if (result <= 0) {
                throw new BusinessException("冻结账户余额失败");
            }
            
            // 记录冻结状态
            String freezeId = getFreezeFundId(actionContext.getXid(), userId);
            FREEZE_RECORDS.put(freezeId, true);
            
            log.info("账户服务TCC-Try阶段成功，XID: {}, userId: {}, money: {}", actionContext.getXid(), userId, money);
            return true;
        } catch (Exception e) {
            log.error("账户服务TCC-Try阶段失败: {}", e.getMessage(), e);
            throw new BusinessException("账户服务TCC-Try阶段失败: " + e.getMessage(), e);
        } finally {
            closeResources(ps, rs);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public boolean commit(BusinessActionContext actionContext) {
        log.info("账户服务TCC-Confirm阶段开始，XID: {}", actionContext.getXid());
        
        String userId = actionContext.getActionContext("userId").toString();
        String freezeId = getFreezeFundId(actionContext.getXid(), userId);
        
        // 检查是否已经处理过
        if (!FREEZE_RECORDS.containsKey(freezeId)) {
            log.info("账户服务TCC-Confirm阶段，冻结记录不存在，可能已经处理过，XID: {}, userId: {}", actionContext.getXid(), userId);
            return true;
        }
        
        try {
            // 确认扣款，实际上在Try阶段已经扣款，这里只需要删除冻结记录
            FREEZE_RECORDS.remove(freezeId);
            log.info("账户服务TCC-Confirm阶段成功，XID: {}, userId: {}", actionContext.getXid(), userId);
            return true;
        } catch (Exception e) {
            log.error("账户服务TCC-Confirm阶段失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean rollback(BusinessActionContext actionContext) {
        log.info("账户服务TCC-Cancel阶段开始，XID: {}", actionContext.getXid());
        
        String userId = actionContext.getActionContext("userId").toString();
        int money = Integer.parseInt(actionContext.getActionContext("money").toString());
        String freezeId = getFreezeFundId(actionContext.getXid(), userId);
        
        // 检查是否已经处理过
        if (!FREEZE_RECORDS.containsKey(freezeId)) {
            log.info("账户服务TCC-Cancel阶段，冻结记录不存在，可能已经处理过，XID: {}, userId: {}", actionContext.getXid(), userId);
            return true;
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 恢复金额
            String sql = "UPDATE account SET money = money + ? WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, money);
            ps.setString(2, userId);
            ps.executeUpdate();
            
            // 删除冻结记录
            FREEZE_RECORDS.remove(freezeId);
            
            log.info("账户服务TCC-Cancel阶段成功，XID: {}, userId: {}, money: {}", actionContext.getXid(), userId, money);
            return true;
        } catch (Exception e) {
            log.error("账户服务TCC-Cancel阶段失败: {}", e.getMessage(), e);
            return false;
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * 获取冻结资金ID
     *
     * @param xid    事务ID
     * @param userId 用户ID
     * @return 冻结资金ID
     */
    private String getFreezeFundId(String xid, String userId) {
        return xid + "-" + userId;
    }
    
    /**
     * 关闭资源
     *
     * @param ps PreparedStatement
     * @param rs ResultSet
     */
    private void closeResources(PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            log.error("关闭资源失败: {}", e.getMessage(), e);
        }
    }
}