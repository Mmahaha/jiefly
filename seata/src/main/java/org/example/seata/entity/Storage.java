package org.example.seata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 库存实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Storage {
    /**
     * 库存ID
     */
    private Long id;
    
    /**
     * 商品ID
     */
    private String productId;
    
    /**
     * 库存数量
     */
    private Integer count;
}