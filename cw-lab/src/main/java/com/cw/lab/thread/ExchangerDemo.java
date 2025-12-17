package com.cw.lab.thread;

import java.util.concurrent.Exchanger;

/**
 * @author thisdcw
 * @date 2025年11月06日 15:19
 */
public class ExchangerDemo {

    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                String data = "A的数据";
                System.out.println("线程A 准备交换：" + data);
                String result = exchanger.exchange(data);
                System.out.println("线程A 收到：" + result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程A").start();

        new Thread(() -> {
            try {
                String data = "B的数据";
                System.out.println("线程B 准备交换：" + data);
                String result = exchanger.exchange(data);
                System.out.println("线程B 收到：" + result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程B").start();
    }

}
