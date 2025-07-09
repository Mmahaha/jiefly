package org.example.seata.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.DatabaseConnection;
import org.example.seata.service.StorageService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 库存服务实现类
 */
@Slf4j
public class StorageServiceImpl implements StorageService {
    private static final String DB_NAME = "seata_storage";

    @Override
    public void deduct(String productId, int count) {
        log.info("开始扣减库存，商品ID: {}，数量: {}", productId, count);
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 检查库存是否足够
            int currentCount = getCount(productId);
            if (currentCount < count) {
                throw new BusinessException("库存不足");
            }
            
            // 扣减库存
            String sql = "UPDATE storage SET count = count - ? WHERE product_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, count);
            ps.setString(2, productId);
            
            int result = ps.executeUpdate();
            if (result <= 0) {
                throw new BusinessException("扣减库存失败");
            }
            
            log.info("扣减库存成功，商品ID: {}，数量: {}", productId, count);
        } catch (SQLException e) {
            log.error("扣减库存失败: {}", e.getMessage(), e);
            throw new BusinessException("扣减库存失败: " + e.getMessage(), e);
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public int getCount(String productId) {
        log.info("开始查询库存，商品ID: {}", productId);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            String sql = "SELECT count FROM storage WHERE product_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, productId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt("count");
                log.info("查询库存成功，商品ID: {}，库存: {}", productId, count);
                return count;
            } else {
                throw new BusinessException("商品不存在");
            }
        } catch (SQLException e) {
            log.error("查询库存失败: {}", e.getMessage(), e);
            throw new BusinessException("查询库存失败: " + e.getMessage(), e);
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