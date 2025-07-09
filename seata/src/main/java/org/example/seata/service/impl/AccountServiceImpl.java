package org.example.seata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.DatabaseConnection;
import org.example.seata.service.AccountService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 账户服务实现类
 */
@Slf4j
public class AccountServiceImpl implements AccountService {
    private static final String DB_NAME = "seata_account";

    @Override
    public void deduct(String userId, int money) {
        log.info("开始扣减账户余额，用户ID: {}，金额: {}", userId, money);
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            // 检查余额是否足够
            int balance = getBalance(userId);
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
            
            log.info("扣减账户余额成功，用户ID: {}，金额: {}", userId, money);
        } catch (SQLException e) {
            log.error("扣减账户余额失败: {}", e.getMessage(), e);
            throw new BusinessException("扣减账户余额失败: " + e.getMessage(), e);
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public int getBalance(String userId) {
        log.info("查询账户余额，用户ID: {}", userId);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            String sql = "SELECT money FROM account WHERE user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                int balance = rs.getInt("money");
                log.info("查询账户余额成功，用户ID: {}，余额: {}", userId, balance);
                return balance;
            } else {
                throw new BusinessException("账户不存在");
            }
        } catch (SQLException e) {
            log.error("查询账户余额失败: {}", e.getMessage(), e);
            throw new BusinessException("查询账户余额失败: " + e.getMessage(), e);
        } finally {
            closeResources(ps, rs);
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