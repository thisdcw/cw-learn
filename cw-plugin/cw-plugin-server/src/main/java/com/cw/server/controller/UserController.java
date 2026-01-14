package com.cw.server.controller;

import com.cw.server.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author thisdcw
 * @date 2026年01月08日 16:37
 */
@RestController
@RequestMapping("/user/")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("all")
    public String all() {
        return userService.all();
    }

    @PostMapping("delete")
    public String delete() {
        userService.delete();
        return "success";
    }

}
