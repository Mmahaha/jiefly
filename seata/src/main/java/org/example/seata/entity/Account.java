package org.example.seata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    /**
     * 账户ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 账户余额
     */
    private Integer money;
}