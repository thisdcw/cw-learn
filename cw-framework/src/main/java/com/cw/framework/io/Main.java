package com.cw.framework.io;

import java.io.*;
import java.util.Arrays;

/**
 * @author thisdcw
 * @date 2025年10月29日 15:52
 */
public class Main {

    public static void main(String[] args) throws IOException {

        FileInputStream fis = new FileInputStream(new File("D:\\code\\cw-learn\\cw-framework\\src\\main\\resources\\data\\test.txt"));

        byte[] bytes = fis.readAllBytes();
        System.out.println(new String(bytes));

    }
}
