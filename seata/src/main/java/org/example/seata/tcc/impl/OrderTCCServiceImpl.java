package org.example.seata.tcc.impl;

import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.DatabaseConnection;
import org.example.seata.service.OrderService;
import org.example.seata.service.impl.OrderServiceImpl;
import org.example.seata.tcc.OrderTCCService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单TCC服务实现类
 */
@Slf4j
public class OrderTCCServiceImpl implements OrderTCCService {
    private static final String DB_NAME = "seata_order";
    private static final Map<String, Long> ORDER_RECORDS = new ConcurrentHashMap<>();
    private final OrderService orderService;

    public OrderTCCServiceImpl() {
        this.orderService = new OrderServiceImpl();
    }

    @Override
    public Long prepareCreate(BusinessActionContext actionContext, String userId, String productId, int count) {
        log.info("订单服务TCC-Try阶段开始，XID: {}, userId: {}, productId: {}, count: {}", 
                actionContext.getXid(), userId, productId, count);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 计算订单金额（假设每个商品单价为10）
            int money = count * 10;
            
            // 创建订单（状态为0-创建中）
            Long orderId = orderService.create(userId, productId, count);
            
            // 记录订单状态
            String orderKey = getOrderKey(actionContext.getXid(), userId, productId);
            ORDER_RECORDS.put(orderKey, orderId);
            
            log.info("订单服务TCC-Try阶段成功，XID: {}, orderId: {}", actionContext.getXid(), orderId);
            return orderId;
        } catch (Exception e) {
            log.error("订单服务TCC-Try阶段失败: {}", e.getMessage(), e);
            throw new BusinessException("订单服务TCC-Try阶段失败: " + e.getMessage(), e);
        } finally {
            closeResources(ps, rs);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public boolean commit(BusinessActionContext actionContext) {
        log.info("订单服务TCC-Confirm阶段开始，XID: {}", actionContext.getXid());
        
        String userId = actionContext.getActionContext("userId").toString();
        String productId = actionContext.getActionContext("productId").toString();
        String orderKey = getOrderKey(actionContext.getXid(), userId, productId);
        
        // 检查是否已经处理过
        if (!ORDER_RECORDS.containsKey(orderKey)) {
            log.info("订单服务TCC-Confirm阶段，订单记录不存在，可能已经处理过，XID: {}", actionContext.getXid());
            return true;
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 更新订单状态为已完成
            Long orderId = ORDER_RECORDS.get(orderKey);
            orderService.updateStatus(orderId, 1);
            
            // 删除订单记录
            ORDER_RECORDS.remove(orderKey);
            
            log.info("订单服务TCC-Confirm阶段成功，XID: {}, orderId: {}", actionContext.getXid(), orderId);
            return true;
        } catch (Exception e) {
            log.error("订单服务TCC-Confirm阶段失败: {}", e.getMessage(), e);
            return false;
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public boolean rollback(BusinessActionContext actionContext) {
        log.info("订单服务TCC-Cancel阶段开始，XID: {}", actionContext.getXid());
        
        String userId = actionContext.getActionContext("userId").toString();
        String productId = actionContext.getActionContext("productId").toString();
        String orderKey = getOrderKey(actionContext.getXid(), userId, productId);
        
        // 检查是否已经处理过
        if (!ORDER_RECORDS.containsKey(orderKey)) {
            log.info("订单服务TCC-Cancel阶段，订单记录不存在，可能已经处理过，XID: {}", actionContext.getXid());
            return true;
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 删除订单
            Long orderId = ORDER_RECORDS.get(orderKey);
            String sql = "DELETE FROM orders WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, orderId);
            ps.executeUpdate();
            
            // 删除订单记录
            ORDER_RECORDS.remove(orderKey);
            
            log.info("订单服务TCC-Cancel阶段成功，XID: {}, orderId: {}", actionContext.getXid(), orderId);
            return true;
        } catch (Exception e) {
            log.error("订单服务TCC-Cancel阶段失败: {}", e.getMessage(), e);
            return false;
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * 获取订单键
     *
     * @param xid       事务ID
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 订单键
     */
    private String getOrderKey(String xid, String userId, String productId) {
        return xid + "-" + userId + "-" + productId;
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