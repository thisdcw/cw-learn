package com.cw.demo.finance.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author thisdcw
 * @date 2025年07月31日 20:18
 */
@AllArgsConstructor
@Getter
public enum UserLevel {

    LEVEL_1(1, 0.02),
    LEVEL_2(2, 0.04),
    LEVEL_3(3, 0.06),
    LEVEL_4(4, 0.08),
    ;

    //职级
    private final int level;

    //薪资占业绩比例,用于计算薪资
    private final double radio;


    /**
     * 根据level获取对应的比例
     */
    public static double getRadioByLevel(int level) {
        for (UserLevel userLevel : values()) {
            if (userLevel.level == level) {
                return userLevel.radio;
            }
        }
        return 0;
    }
}
