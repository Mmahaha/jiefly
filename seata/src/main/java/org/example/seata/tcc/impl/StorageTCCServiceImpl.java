package org.example.seata.tcc.impl;

import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.example.seata.common.BusinessException;
import org.example.seata.common.DatabaseConnection;
import org.example.seata.service.StorageService;
import org.example.seata.service.impl.StorageServiceImpl;
import org.example.seata.tcc.StorageTCCService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 库存TCC服务实现类
 */
@Slf4j
public class StorageTCCServiceImpl implements StorageTCCService {
    private static final String DB_NAME = "seata_storage";
    private static final Map<String, Integer> FREEZE_RECORDS = new ConcurrentHashMap<>();
    private final StorageService storageService;

    public StorageTCCServiceImpl() {
        this.storageService = new StorageServiceImpl();
    }

    @Override
    public boolean prepareDeduct(BusinessActionContext actionContext, String productId, int count) {
        log.info("库存服务TCC-Try阶段开始，XID: {}, productId: {}, count: {}", actionContext.getXid(), productId, count);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 检查库存是否足够
            int currentCount = storageService.getCount(productId);
            if (currentCount < count) {
                throw new BusinessException("库存不足");
            }
            
            // 冻结库存（预留资源）
            String sql = "UPDATE storage SET count = count - ? WHERE product_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, count);
            ps.setString(2, productId);
            int result = ps.executeUpdate();
            
            if (result <= 0) {
                throw new BusinessException("冻结库存失败");
            }
            
            // 记录冻结状态
            String freezeId = getFreezeStockId(actionContext.getXid(), productId);
            FREEZE_RECORDS.put(freezeId, count);
            
            log.info("库存服务TCC-Try阶段成功，XID: {}, productId: {}, count: {}", actionContext.getXid(), productId, count);
            return true;
        } catch (Exception e) {
            log.error("库存服务TCC-Try阶段失败: {}", e.getMessage(), e);
            throw new BusinessException("库存服务TCC-Try阶段失败: " + e.getMessage(), e);
        } finally {
            closeResources(ps, rs);
            DatabaseConnection.closeConnection(conn);
        }
    }

    @Override
    public boolean commit(BusinessActionContext actionContext) {
        log.info("库存服务TCC-Confirm阶段开始，XID: {}", actionContext.getXid());
        
        String productId = actionContext.getActionContext("productId").toString();
        String freezeId = getFreezeStockId(actionContext.getXid(), productId);
        
        // 检查是否已经处理过
        if (!FREEZE_RECORDS.containsKey(freezeId)) {
            log.info("库存服务TCC-Confirm阶段，冻结记录不存在，可能已经处理过，XID: {}, productId: {}", actionContext.getXid(), productId);
            return true;
        }
        
        try {
            // 确认扣减库存，实际上在Try阶段已经扣减，这里只需要删除冻结记录
            FREEZE_RECORDS.remove(freezeId);
            log.info("库存服务TCC-Confirm阶段成功，XID: {}, productId: {}", actionContext.getXid(), productId);
            return true;
        } catch (Exception e) {
            log.error("库存服务TCC-Confirm阶段失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean rollback(BusinessActionContext actionContext) {
        log.info("库存服务TCC-Cancel阶段开始，XID: {}", actionContext.getXid());
        
        String productId = actionContext.getActionContext("productId").toString();
        String freezeId = getFreezeStockId(actionContext.getXid(), productId);
        
        // 检查是否已经处理过
        if (!FREEZE_RECORDS.containsKey(freezeId)) {
            log.info("库存服务TCC-Cancel阶段，冻结记录不存在，可能已经处理过，XID: {}, productId: {}", actionContext.getXid(), productId);
            return true;
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConnection.getConnection(DB_NAME);
            
            // 恢复库存
            int count = FREEZE_RECORDS.get(freezeId);
            String sql = "UPDATE storage SET count = count + ? WHERE product_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, count);
            ps.setString(2, productId);
            ps.executeUpdate();
            
            // 删除冻结记录
            FREEZE_RECORDS.remove(freezeId);
            
            log.info("库存服务TCC-Cancel阶段成功，XID: {}, productId: {}, count: {}", actionContext.getXid(), productId, count);
            return true;
        } catch (Exception e) {
            log.error("库存服务TCC-Cancel阶段失败: {}", e.getMessage(), e);
            return false;
        } finally {
            closeResources(ps, null);
            DatabaseConnection.closeConnection(conn);
        }
    }
    
    /**
     * 获取冻结库存ID
     *
     * @param xid       事务ID
     * @param productId 商品ID
     * @return 冻结库存ID
     */
    private String getFreezeStockId(String xid, String productId) {
        return xid + "-" + productId;
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