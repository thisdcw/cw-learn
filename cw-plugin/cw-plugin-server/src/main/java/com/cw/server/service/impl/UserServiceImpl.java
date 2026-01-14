package com.cw.server.service.impl;

import com.cw.server.biz.AbstractPlugin;
import com.cw.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author thisdcw
 * @date 2026年01月12日 15:30
 */
@Slf4j
@Service
public class UserServiceImpl extends AbstractPlugin implements UserService {

    @Override
    public String all() {
        checkAllow();
        log.info("获取所有用户");
        return "获取所有用户";
    }

    @Override
    public void delete() {
        checkAllow();
        log.info("删除用户");
    }
}
