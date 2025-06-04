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

   public static final  Logger log = LoggerFactory.getLogger(LicenseValidator.class);

    private final TimeLockService timeLockService;
    
    @Value("${app.license.key:default-license-key-for-development}")
    private String licenseKey;
    
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("应用启动，开始验证许可证...");
        
        boolean valid = timeLockService.validateLicense(licenseKey);
        
        if (!valid) {
            log.error("许可证验证失败，应用将停止运行");
            
            // 获取Spring上下文并关闭它，优雅地终止应用
            ConfigurableApplicationContext context = event.getApplicationContext();
            
            // 打印机器ID，用于生成新的许可证
            String machineId = timeLockService.getMachineId();
            if (machineId != null) {
                log.error("本机机器ID: {}", machineId);
                log.error("请使用此机器ID申请有效的许可证");
            }
            
            // 设置退出码并关闭应用
            int exitCode = SpringApplication.exit(context, () -> 1);
            System.exit(exitCode);
        } else {
            log.info("许可证验证成功，应用将继续运行");
        }
    }
} 