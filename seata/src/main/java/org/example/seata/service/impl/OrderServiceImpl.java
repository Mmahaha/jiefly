package org.example.seata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.DatabaseConnection;
import org.example.seata.entity.Order;
import org.example.seata.service.OrderService;

import java.sql.*;

/**
 * 订单服务实现类
 */
@Slf4j
public class OrderServiceImpl implements OrderService {
    private static final String DB_NAME = "seata_order";

    @Override
    public Long create(String userId, String productId, int count) {
        log.info("开始创建订单，用户ID: {}，商品ID: {}，数量: {}", userId, productId, count);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 计算订单金额（假设每个商品单价为10）
            int money = count * 10;
            
            // 创建订单
            String sql = "INSERT INTO orders (user_id, product_id, count, money, status) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userId);
            ps.setString(2, productId);
            ps.setInt(3, count);
            ps.setInt(4, money);
            ps.setInt(5, 0); // 0-创建中
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new BusinessException("创建订单失败");
            }
            
            // 获取生成的订单ID
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Long orderId = rs.getLong(1);
                log.info("创建订单成功，订单ID: {}", orderId);
                return orderId;
            } else {
                throw new BusinessException("获取订单ID失败");
            }
        } catch (SQLException e) {
            log.error("创建订单失败: {}", e.getMessage(), e);
            throw new BusinessException("创建订单失败: " + e.getMessage(), e);
        } finally {
            closeResources(ps, rs);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public void updateStatus(Long orderId, int status) {
        log.info("开始更新订单状态，订单ID: {}，状态: {}", orderId, status);
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            String sql = "UPDATE orders SET status = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setLong(2, orderId);
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new BusinessException("更新订单状态失败");
            }
            
            log.info("更新订单状态成功，订单ID: {}，状态: {}", orderId, status);
        } catch (SQLException e) {
            log.error("更新订单状态失败: {}", e.getMessage(), e);
            throw new BusinessException("更新订单状态失败: " + e.getMessage(), e);
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public Order getById(Long orderId) {
        log.info("开始查询订单，订单ID: {}", orderId);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            String sql = "SELECT * FROM orders WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setUserId(rs.getString("user_id"));
                order.setProductId(rs.getString("product_id"));
                order.setCount(rs.getInt("count"));
                order.setMoney(rs.getInt("money"));
                order.setStatus(rs.getInt("status"));
                
                log.info("查询订单成功，订单ID: {}", orderId);
                return order;
            } else {
                throw new BusinessException("订单不存在");
            }
        } catch (SQLException e) {
            log.error("查询订单失败: {}", e.getMessage(), e);
            throw new BusinessException("查询订单失败: " + e.getMessage(), e);
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