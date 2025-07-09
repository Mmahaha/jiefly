package org.example.seata.saga.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.DatabaseConnection;
import org.example.seata.saga.StorageSagaService;
import org.example.seata.service.StorageService;
import org.example.seata.service.impl.StorageServiceImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 库存SAGA服务实现类
 */
@Slf4j
public class StorageSagaServiceImpl implements StorageSagaService {
    private static final String DB_NAME = "seata_storage";
    private final StorageService storageService;

    public StorageSagaServiceImpl() {
        this.storageService = new StorageServiceImpl();
    }

    @Override
    public boolean deduct(String productId, int count) {
        log.info("库存服务SAGA-扣减库存，商品ID: {}，数量: {}", productId, count);
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 检查库存是否足够
            int currentCount = storageService.getCount(productId);
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
            
            log.info("库存服务SAGA-扣减库存成功，商品ID: {}，数量: {}", productId, count);
            return true;
        } catch (Exception e) {
            log.error("库存服务SAGA-扣减库存失败: {}", e.getMessage(), e);
            throw new BusinessException("库存服务SAGA-扣减库存失败: " + e.getMessage(), e);
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public boolean compensateDeduct(String productId, int count) {
        log.info("库存服务SAGA-补偿：恢复库存，商品ID: {}，数量: {}", productId, count);
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 恢复库存
            String sql = "UPDATE storage SET count = count + ? WHERE product_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, count);
            ps.setString(2, productId);
            int result = ps.executeUpdate();
            
            if (result <= 0) {
                log.error("库存服务SAGA-补偿：恢复库存失败，商品ID: {}，数量: {}", productId, count);
                return false;
            }
            
            log.info("库存服务SAGA-补偿：恢复库存成功，商品ID: {}，数量: {}", productId, count);
            return true;
        } catch (Exception e) {
            log.error("库存服务SAGA-补偿：恢复库存失败: {}", e.getMessage(), e);
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