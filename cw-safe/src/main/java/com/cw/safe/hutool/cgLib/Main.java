package com.cw.safe.hutool.cgLib;

import cn.hutool.extra.cglib.CglibUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author thisdcw
 * @date 2025年05月31日 9:58
 */
public class Main {

    public static void main(String[] args) {

        Object from = new FromUser("张三", 19);
        Object to = new ToUser(180);
        CglibUtil.copy(from, to);

        System.out.println(JSONUtil.parse(to));
    }
}