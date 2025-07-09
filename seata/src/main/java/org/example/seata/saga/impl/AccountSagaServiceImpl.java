package org.example.seata.saga.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.DatabaseConnection;
import org.example.seata.saga.AccountSagaService;
import org.example.seata.service.AccountService;
import org.example.seata.service.impl.AccountServiceImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 账户SAGA服务实现类
 */
@Slf4j
public class AccountSagaServiceImpl implements AccountSagaService {
    private static final String DB_NAME = "seata_account";
    private final AccountService accountService;

    public AccountSagaServiceImpl() {
        this.accountService = new AccountServiceImpl();
    }

    @Override
    public boolean deduct(String userId, int money) {
        log.info("账户服务SAGA-扣减账户余额，用户ID: {}，金额: {}", userId, money);
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 检查余额是否足够
            int balance = accountService.getBalance(userId);
            if (balance < money) {
                throw new BusinessException("账户余额不足");
            }
            
            // 扣减余额
            String sql = "UPDATE account SET money = money - ? WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, money);
            ps.setString(2, userId);
            int result = ps.executeUpdate();
            
            if (result <= 0) {
                throw new BusinessException("扣减账户余额失败");
            }
            
            log.info("账户服务SAGA-扣减账户余额成功，用户ID: {}，金额: {}", userId, money);
            return true;
        } catch (Exception e) {
            log.error("账户服务SAGA-扣减账户余额失败: {}", e.getMessage(), e);
            throw new BusinessException("账户服务SAGA-扣减账户余额失败: " + e.getMessage(), e);
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public boolean compensateDeduct(String userId, int money) {
        log.info("账户服务SAGA-补偿：恢复账户余额，用户ID: {}，金额: {}", userId, money);
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 恢复余额
            String sql = "UPDATE account SET money = money + ? WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, money);
            ps.setString(2, userId);
            int result = ps.executeUpdate();
            
            if (result <= 0) {
                log.error("账户服务SAGA-补偿：恢复账户余额失败，用户ID: {}，金额: {}", userId, money);
                return false;
            }
            
            log.info("账户服务SAGA-补偿：恢复账户余额成功，用户ID: {}，金额: {}", userId, money);
            return true;
        } catch (Exception e) {
            log.error("账户服务SAGA-补偿：恢复账户余额失败: {}", e.getMessage(), e);
            return false;
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
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