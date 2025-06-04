package com.cw.framework.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 许可证信息类，包含许可证的基本信息
 * @author Admin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseInfo {
    
    /**
     * 许可证密钥
     */
    private String licenseKey;
    
    /**
     * 客户名称
     */
    private String customerName;
    
    /**
     * 发行日期
     */
    private Date issueDate;
    
    /**
     * 过期日期
     */
    private Date expiryDate;
    
    /**
     * 机器ID，用于绑定特定机器
     */
    private String machineId;
    
    /**
     * 许可证类型（例如：试用版、标准版、企业版）
     */
    private String licenseType;
    
    /**
     * 许可证状态
     */
    private LicenseStatus status;
    
    /**
     * 许可证状态枚举
     */
    public enum LicenseStatus {
        VALID,      // 有效
        EXPIRED,    // 已过期
        INVALID     // 无效
    }
} 