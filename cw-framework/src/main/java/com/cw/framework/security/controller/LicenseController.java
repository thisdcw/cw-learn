package com.cw.framework.security.controller;

import com.cw.framework.security.LicenseInfo;
import com.cw.framework.security.TimeLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 许可证控制器
 */
@RestController
@RequestMapping("/api/license")
@RequiredArgsConstructor
public class LicenseController {
    
    private static final Logger log = LoggerFactory.getLogger(LicenseController.class);
    
    private final TimeLockService timeLockService;
    
    @Value("${app.license.key:default-license-key-for-development}")
    private String licenseKey;
    
    /**
     * 验证许可证
     */
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateLicense() {
        boolean valid = timeLockService.validateLicense(licenseKey);
        return ResponseEntity.ok(valid);
    }
    
    /**
     * 获取当前机器ID
     */
    @GetMapping("/machine-id")
    public ResponseEntity<String> getMachineId() {
        String machineId = timeLockService.getMachineId();
        if (machineId == null) {
            return ResponseEntity.badRequest().body("无法获取机器ID");
        }
        return ResponseEntity.ok(machineId);
    }
    
    /**
     * 使用指定的过期日期验证许可证
     */
    @PostMapping("/validate-with-date")
    public ResponseEntity<Boolean> validateLicenseWithDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date expiryDate,
            @RequestParam(defaultValue = "${app.license.key:default-license-key-for-development}") String licenseKey) {
        
        boolean valid = timeLockService.validateLicense(expiryDate, licenseKey);
        return ResponseEntity.ok(valid);
    }
    
    /**
     * 设置许可证
     */
    @PostMapping("/set")
    public ResponseEntity<Map<String, Object>> setLicense(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date expiryDate,
            @RequestParam String licenseKey) {
        
        boolean success = timeLockService.setLicense(expiryDate, licenseKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "许可证设置成功" : "许可证设置失败");
        response.put("expiryDate", expiryDate);
        response.put("machineId", timeLockService.getMachineId());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 重置许可证（仅用于测试）
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetLicense() {
        boolean success = timeLockService.resetLicense();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "许可证重置成功" : "许可证重置失败");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取许可证信息
     */
    @GetMapping("/info")
    public ResponseEntity<LicenseInfo> getLicenseInfo() {
        // 默认设置为一年后过期
        Date expiryDate = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        
        boolean valid = timeLockService.validateLicense(expiryDate, licenseKey);
        
        LicenseInfo licenseInfo = LicenseInfo.builder()
                .licenseKey(licenseKey)
                .customerName("测试客户")
                .issueDate(new Date())
                .expiryDate(expiryDate)
                .machineId(timeLockService.getMachineId())
                .licenseType("标准版")
                .status(valid ? LicenseInfo.LicenseStatus.VALID : LicenseInfo.LicenseStatus.INVALID)
                .build();
        
        return ResponseEntity.ok(licenseInfo);
    }
} 