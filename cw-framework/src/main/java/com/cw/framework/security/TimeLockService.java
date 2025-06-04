package com.cw.framework.security;

import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 时间锁服务，提供时间锁的验证和管理功能
 */
@Service
public class TimeLockService {

    public static final Logger log = LoggerFactory.getLogger(TimeLockService.class);
    private static final int MACHINE_ID_BUFFER_SIZE = 256;
    private final TimeLockLibrary timeLockLibrary;

    public TimeLockService() {
        this.timeLockLibrary = TimeLockLibrary.INSTANCE;
    }

    /**
     * 验证应用是否在有效期内
     *
     * @param licenseKey 许可证密钥
     * @return 如果在有效期内返回true，否则返回false
     */
    public boolean validateLicense(String licenseKey) {
        try {
            // 默认设置为一年后过期
            Date expireDate = DateUtil.offsetMonth(new Date(), 12);
            String expireDateStr = DateUtil.format(expireDate, "yyyy-MM-dd");

            log.info("验证许可证，过期时间: {}", expireDateStr);
            boolean valid = timeLockLibrary.validateTimeLock(expireDateStr, licenseKey);

            if (!valid) {
                log.warn("许可证验证失败，应用将停止运行");
            } else {
                log.info("许可证验证成功，应用正常运行");
            }

            return valid;
        } catch (Exception e) {
            log.error("验证许可证时发生错误", e);
            return false;
        }
    }

    /**
     * 使用指定的过期日期验证许可证
     *
     * @param expireDate 过期日期
     * @param licenseKey 许可证密钥
     * @return 如果在有效期内返回true，否则返回false
     */
    public boolean validateLicense(Date expireDate, String licenseKey) {
        try {
            String expireDateStr = DateUtil.format(expireDate, "yyyy-MM-dd");

            log.info("验证许可证，过期时间: {}", expireDateStr);
            boolean valid = timeLockLibrary.validateTimeLock(expireDateStr, licenseKey);

            if (!valid) {
                log.warn("许可证验证失败，应用将停止运行");
            } else {
                log.info("许可证验证成功，应用正常运行");
            }

            return valid;
        } catch (Exception e) {
            log.error("验证许可证时发生错误", e);
            return false;
        }
    }
    
    /**
     * 设置许可证，将过期日期和许可证密钥保存到系统中
     *
     * @param expireDate 过期日期
     * @param licenseKey 许可证密钥
     * @return 如果设置成功返回true，否则返回false
     */
    public boolean setLicense(Date expireDate, String licenseKey) {
        try {
            String expireDateStr = DateUtil.format(expireDate, "yyyy-MM-dd");
            
            log.info("设置许可证，过期时间: {}", expireDateStr);
            boolean success = timeLockLibrary.setLicense(expireDateStr, licenseKey);
            
            if (success) {
                log.info("许可证设置成功");
            } else {
                log.warn("许可证设置失败");
            }
            
            return success;
        } catch (Exception e) {
            log.error("设置许可证时发生错误", e);
            return false;
        }
    }
    
    /**
     * 重置许可证（仅用于测试）
     *
     * @return 如果重置成功返回true，否则返回false
     */
    public boolean resetLicense() {
        try {
            log.info("重置许可证");
            boolean success = timeLockLibrary.resetLicense();
            
            if (success) {
                log.info("许可证重置成功");
            } else {
                log.warn("许可证重置失败");
            }
            
            return success;
        } catch (Exception e) {
            log.error("重置许可证时发生错误", e);
            return false;
        }
    }

    /**
     * 获取当前机器的唯一标识符
     *
     * @return 机器标识符，如果获取失败则返回null
     */
    public String getMachineId() {
        try {
            byte[] buffer = new byte[MACHINE_ID_BUFFER_SIZE];
            boolean success = timeLockLibrary.getMachineId(buffer, buffer.length);

            if (success) {
                // 转换字节数组为字符串，去除末尾的空字符
                int length = 0;
                while (length < buffer.length && buffer[length] != 0) {
                    length++;
                }

                return new String(buffer, 0, length);
            } else {
                log.error("获取机器ID失败");
                return null;
            }
        } catch (Exception e) {
            log.error("获取机器ID时发生错误", e);
            return null;
        }
    }
} 