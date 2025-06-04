package com.cw.framework.user.service.impl;

import com.cw.framework.security.TimeLockService;
import com.cw.framework.user.model.User;
import com.cw.framework.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    
    private final TimeLockService timeLockService;
    
    @Value("${app.license.key:default-license-key-for-development}")
    private String licenseKey;
    
    // 模拟用户数据库
    private final Map<Long, User> userMap = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @PostConstruct
    public void init() {
        // 初始化一些测试用户
        createUser(User.builder()
                .username("admin")
                .password("123456")
                .fullName("系统管理员")
                .email("admin@example.com")
                .phone("13800138000")
                .createTime(new Date())
                .status(1)
                .build());
        
        createUser(User.builder()
                .username("user1")
                .password("123456")
                .fullName("测试用户1")
                .email("user1@example.com")
                .phone("13800138001")
                .createTime(new Date())
                .status(1)
                .build());
    }
    
    @Override
    public List<User> getAllUsers() {
        // 每次操作前验证许可证
        if (!timeLockService.validateLicense(licenseKey)) {
            log.error("许可证验证失败，无法执行操作");
            throw new RuntimeException("许可证已过期或无效，请联系管理员");
        }
        
        return new ArrayList<>(userMap.values());
    }
    
    @Override
    public User getUserById(Long id) {
        // 每次操作前验证许可证
        if (!timeLockService.validateLicense(licenseKey)) {
            log.error("许可证验证失败，无法执行操作");
            throw new RuntimeException("许可证已过期或无效，请联系管理员");
        }
        
        return userMap.get(id);
    }
    
    @Override
    public User createUser(User user) {
        // 每次操作前验证许可证
        if (!timeLockService.validateLicense(licenseKey)) {
            log.error("许可证验证失败，无法执行操作");
            throw new RuntimeException("许可证已过期或无效，请联系管理员");
        }
        
        // 设置ID和创建时间
        user.setId(idGenerator.getAndIncrement());
        
        if (user.getCreateTime() == null) {
            user.setCreateTime(new Date());
        }
        
        userMap.put(user.getId(), user);
        return user;
    }
    
    @Override
    public User updateUser(Long id, User user) {
        // 每次操作前验证许可证
        if (!timeLockService.validateLicense(licenseKey)) {
            log.error("许可证验证失败，无法执行操作");
            throw new RuntimeException("许可证已过期或无效，请联系管理员");
        }
        
        if (!userMap.containsKey(id)) {
            return null;
        }
        
        User existingUser = userMap.get(id);
        
        // 更新用户信息，保留ID和创建时间
        user.setId(id);
        user.setCreateTime(existingUser.getCreateTime());
        
        userMap.put(id, user);
        return user;
    }
    
    @Override
    public boolean deleteUser(Long id) {
        // 每次操作前验证许可证
        if (!timeLockService.validateLicense(licenseKey)) {
            log.error("许可证验证失败，无法执行操作");
            throw new RuntimeException("许可证已过期或无效，请联系管理员");
        }
        
        return userMap.remove(id) != null;
    }
} 