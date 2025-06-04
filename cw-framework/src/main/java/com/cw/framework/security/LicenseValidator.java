package com.cw.framework.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 许可证验证器，在应用启动时验证许可证
 * @author Admin
 */
@Component
@RequiredArgsConstructor
public class LicenseValidator implements ApplicationListener<ApplicationStartedEvent> {

   public static final Logger log = LoggerFactory.getLogger(LicenseValidator.class);

    private final TimeLockService timeLockService;
    
    @Value("${app.license.key:}")
    private String licenseKey;
    
    @Value("${app.license.bypass:false}")
    private boolean bypassLicenseCheck;
    
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("应用启动，开始验证许可证...");
        
        // 如果配置了绕过许可证检查（仅用于开发环境）
        if (bypassLicenseCheck) {
            log.warn("许可证检查已被绕过，这仅应在开发环境中使用！");
            return;
        }
        
        // 如果没有配置许可证密钥
        if (licenseKey == null || licenseKey.trim().isEmpty()) {
            handleLicenseFailure("未配置许可证密钥", event.getApplicationContext());
            return;
        }
        
        boolean valid = timeLockService.validateLicense(licenseKey);
        
        if (!valid) {
            handleLicenseFailure("许可证验证失败", event.getApplicationContext());
        } else {
            log.info("许可证验证成功，应用将继续运行");
        }
    }
    
    /**
     * 处理许可证验证失败的情况
     * 
     * @param errorMessage 错误消息
     * @param context Spring上下文
     */
    private void handleLicenseFailure(String errorMessage, ConfigurableApplicationContext context) {
        log.error("许可证错误: {}, 应用将停止运行", errorMessage);
        
        // 获取机器ID，用于生成新的许可证
        String machineId = timeLockService.getMachineId();
        if (machineId != null) {
            log.error("本机机器ID: {}", machineId);
            log.error("请使用此机器ID申请有效的许可证");
            
            // 尝试生成一个示例许可证密钥（仅用于开发/测试）
            try {
                String sampleLicenseKey = timeLockService.generateLicenseKey(machineId);
                if (sampleLicenseKey != null) {
                    log.error("开发测试用许可证密钥示例: {}", sampleLicenseKey);
                    log.error("请在application.properties或application.yml中设置app.license.key属性");
                }
            } catch (Exception e) {
                log.error("无法生成示例许可证密钥", e);
            }
        }
        
        // 设置退出码并关闭应用
        int exitCode = SpringApplication.exit(context, () -> 1);
        System.exit(exitCode);
    }
} 