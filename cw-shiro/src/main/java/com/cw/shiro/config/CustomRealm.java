package com.cw.shiro.config;

import com.cw.shiro.service.UserService;
import jakarta.annotation.Resource;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * @author thisdcw
 */
public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //mock get user from database
        userService.mockException();

        return new SimpleAuthenticationInfo("admin", "123456", getName());
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }
}

