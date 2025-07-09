package org.example.seata.saga.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.DatabaseConnection;
import org.example.seata.saga.OrderSagaService;
import org.example.seata.service.OrderService;
import org.example.seata.service.impl.OrderServiceImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 订单SAGA服务实现类
 */
@Slf4j
public class OrderSagaServiceImpl implements OrderSagaService {
    private static final String DB_NAME = "seata_order";
    private final OrderService orderService;

    public OrderSagaServiceImpl() {
        this.orderService = new OrderServiceImpl();
    }

    @Override
    public Long create(String userId, String productId, int count) {
        log.info("订单服务SAGA-创建订单，用户ID: {}，商品ID: {}，数量: {}", userId, productId, count);
        
        try {
            // 创建订单
            Long orderId = orderService.create(userId, productId, count);
            log.info("订单服务SAGA-创建订单成功，订单ID: {}", orderId);
            return orderId;
        } catch (Exception e) {
            log.error("订单服务SAGA-创建订单失败: {}", e.getMessage(), e);
            throw new BusinessException("订单服务SAGA-创建订单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean compensateCreate(Long orderId) {
        log.info("订单服务SAGA-补偿：删除订单，订单ID: {}", orderId);
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 删除订单
            String sql = "DELETE FROM orders WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            int result = ps.executeUpdate();
            
            if (result <= 0) {
                log.error("订单服务SAGA-补偿：删除订单失败，订单ID: {}", orderId);
                return false;
            }
            
            log.info("订单服务SAGA-补偿：删除订单成功，订单ID: {}", orderId);
            return true;
        } catch (Exception e) {
            log.error("订单服务SAGA-补偿：删除订单失败: {}", e.getMessage(), e);
            return false;
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public boolean updateStatus(Long orderId, int status) {
        log.info("订单服务SAGA-更新订单状态，订单ID: {}，状态: {}", orderId, status);
        
        try {
            orderService.updateStatus(orderId, status);
            log.info("订单服务SAGA-更新订单状态成功，订单ID: {}，状态: {}", orderId, status);
            return true;
        } catch (Exception e) {
            log.error("订单服务SAGA-更新订单状态失败: {}", e.getMessage(), e);
            return false;
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