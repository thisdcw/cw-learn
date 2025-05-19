package com.cw.shiro.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author thisdcw
 */
@RestController
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            subject.login(token);
        }
        // Because there is an exception in the authenticator, success should not be returned here, but it is returned
        return "login success";
    }
}
