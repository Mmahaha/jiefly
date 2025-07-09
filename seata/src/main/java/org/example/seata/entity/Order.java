package org.example.seata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 商品ID
     */
    private String productId;
    
    /**
     * 商品数量
     */
    private Integer count;
    
    /**
     * 订单金额
     */
    private Integer money;
    
    /**
     * 订单状态：0-创建中，1-已完成
     */
    private Integer status;
}