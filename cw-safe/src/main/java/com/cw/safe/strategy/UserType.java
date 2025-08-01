package com.cw.safe.strategy;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.IntPredicate;

/**
 * @author thisdcw
 * @date 2025年06月26日 20:55
 */
@AllArgsConstructor
@Getter
public enum UserType {
    DEFAULT(type -> type == 10),
    VIP_USER(type -> type == 20),
    SVIP_USER(type -> type == 30),
    SSVIP_USER(type -> type == 40),
    VIP_STORE(type -> type == 50),
    SVIP_STORE(type -> type == 60),
    SSVIP_STORE(type -> type == 70);

    private final IntPredicate support;

    public static UserType of(int userType) {
        for (UserType value : values()) {
            if (value.support.test(userType)) {
                return value;
            }
        }
        return null;
    }
}
