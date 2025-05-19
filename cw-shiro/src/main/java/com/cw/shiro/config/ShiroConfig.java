package com.cw.shiro.config;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author thisdcw
 */
@Configuration
public class ShiroConfig {

    @Bean
    public Realm realm() {
        return new CustomRealm();
    }

    @Bean
    public SecurityManager securityManager(Realm realm) {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setRealm(realm);
        return securityManager;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chain = new DefaultShiroFilterChainDefinition();
        chain.addPathDefinition("/login", "anon");
        chain.addPathDefinition("/**", "authc");
        return chain;
    }
}

