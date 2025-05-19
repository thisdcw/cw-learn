package com.cw.shiro.service;

import com.cw.shiro.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author thisdcw
 */
@Service
public class UserService {

    public void mockException() {
        User user = new User();
        User user1 = new User();

        // exception: Source must not be null
        BeanUtils.copyProperties(user, user1);
    }

}
