package com.cw.framework.thread;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author thisdcw
 * @date 2025年11月06日 10:26
 */
public class CopyOnWriteArrayListExample {

    public static void main(String[] args) throws InterruptedException {
        List<String> list = new CopyOnWriteArrayList<>();

        // 写线程：添加元素
        Runnable writer = () -> {
            for (int i = 0; i < 5; i++) {
                list.add(Thread.currentThread().getName() + "-item" + i);
                System.out.println(Thread.currentThread().getName() + " 添加 item" + i);
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            }
        };

        // 读线程：遍历列表
        Runnable reader = () -> {
            for (int i = 0; i < 5; i++) {
                for (String s : list) {
                    System.out.println(Thread.currentThread().getName() + " 读到 " + s);
                }
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        };

        Thread t1 = new Thread(writer, "Writer-1");
        Thread t2 = new Thread(writer, "Writer-2");
        Thread t3 = new Thread(reader, "Reader-1");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("最终列表内容：" + list);
    }

}
