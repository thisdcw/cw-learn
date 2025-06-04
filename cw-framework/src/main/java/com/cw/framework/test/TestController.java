package com.cw.framework.test;

import com.cw.framework.security.TimeLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器，用于演示时间锁功能
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    
    private final TimeLockService timeLockService;
    
    @Value("${app.license.key:default-license-key-for-development}")
    private String licenseKey;
    
    public TestController(TimeLockService timeLockService) {
        this.timeLockService = timeLockService;
    }
    
    /**
     * 测试创建一个1分钟后过期的许可证
     */
    @PostMapping("/quick-expire")
    public ResponseEntity<Map<String, Object>> createQuickExpireLicense() {
        // 创建一个1分钟后过期的许可证
        Date expireDate = new Date(System.currentTimeMillis() + 60 * 1000); // 1分钟后
        
        boolean success = timeLockService.setLicense(expireDate, licenseKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "已创建1分钟后过期的许可证" : "创建许可证失败");
        response.put("expiryDate", expireDate);
        response.put("machineId", timeLockService.getMachineId());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 测试创建一个已过期的许可证
     */
    @PostMapping("/expired")
    public ResponseEntity<Map<String, Object>> createExpiredLicense() {
        // 创建一个已经过期的许可证（1分钟前）
        Date expireDate = new Date(System.currentTimeMillis() - 60 * 1000); // 1分钟前
        
        boolean success = timeLockService.setLicense(expireDate, licenseKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "已创建过期的许可证" : "创建许可证失败");
        response.put("expiryDate", expireDate);
        response.put("machineId", timeLockService.getMachineId());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 创建有效期为指定天数的许可证
     */
    @PostMapping("/days-valid/{days}")
    public ResponseEntity<Map<String, Object>> createDaysValidLicense(@PathVariable int days) {
        // 创建一个指定天数后过期的许可证
        Date expireDate = new Date(System.currentTimeMillis() + (long) days * 24 * 60 * 60 * 1000);
        
        boolean success = timeLockService.setLicense(expireDate, licenseKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "已创建" + days + "天后过期的许可证" : "创建许可证失败");
        response.put("expiryDate", expireDate);
        response.put("machineId", timeLockService.getMachineId());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 重置许可证
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetLicense() {
        boolean success = timeLockService.resetLicense();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "许可证已重置" : "重置许可证失败");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 验证许可证
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateLicense() {
        boolean valid = timeLockService.validateLicense(licenseKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        response.put("message", valid ? "许可证有效" : "许可证无效或已过期");
        response.put("machineId", timeLockService.getMachineId());
        
        return ResponseEntity.ok(response);
    }
} 