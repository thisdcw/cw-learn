package com.cw.shiro;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author thisdcw
 */
@SpringBootApplication
public class CwShiroApplication {

    @Resource
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(CwShiroApplication.class, args);
    }

    @PostConstruct
    public void setSecurityManager() {
        SecurityManager securityManager = ThreadContext.getSecurityManager();
        if (securityManager == null) {
            SecurityUtils.setSecurityManager(applicationContext.getBean(SecurityManager.class));
        }
    }
}
