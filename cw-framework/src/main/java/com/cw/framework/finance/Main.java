package com.cw.framework.finance;


import com.cw.framework.finance.entity.UserNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author thisdcw
 * @date 2025年07月31日 20:22
 */
public class Main {

    /**
     * A 3
     * B 2
     * C 4     160 120 100
     * D 1
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder().create();
        InputStream is = Main.class.getClassLoader().getResourceAsStream("user.json");
        if (is == null) {
            throw new FileNotFoundException("user.json not found in classpath");
        }
        Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        UserNode root = gson.fromJson(reader, UserNode.class);

        System.out.println(root);

        // 计算薪资
        SalaryCalculator calculator = new SalaryCalculator();
        calculator.calculateAllSalary(root);

        // 打印结果
        calculator.printSalaryDetails(root);
    }
}
