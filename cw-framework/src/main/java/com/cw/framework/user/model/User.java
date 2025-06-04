package com.cw.framework.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户模型类，演示业务模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（通常不应该直接返回，这里仅用于示例）
     */
    private String password;
    
    /**
     * 用户全名
     */
    private String fullName;
    
    /**
     * 电子邮件
     */
    private String email;
    
    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    
    /**
     * 用户状态（0：禁用，1：启用）
     */
    private Integer status;
} 