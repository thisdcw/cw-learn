package com.cw.framework.user.service;

import com.cw.framework.user.model.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取所有用户
     * 
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    User getUserById(Long id);
    
    /**
     * 创建新用户
     * 
     * @param user 用户对象
     * @return 创建后的用户对象
     */
    User createUser(User user);
    
    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User updateUser(Long id, User user);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 是否成功删除
     */
    boolean deleteUser(Long id);
} 