package com.cw.framework.thread;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author thisdcw
 * @date 2025年11月06日 9:42
 */
public class ConcurrentHashMapExample {

    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // computeIfAbsent：仅当 key 不存在时计算并放入
        Runnable computeTask = () -> {
            for (int i = 0; i < 1000; i++) {
                map.computeIfAbsent("key" + (i % 10), k -> {
                    System.out.println(Thread.currentThread().getName() + " 初始化 " + k);
                    return 1;
                });
            }
        };

        // merge：原子更新 key 的值
        Runnable mergeTask = () -> {
            for (int i = 0; i < 1000; i++) {
                //这里如果键不存在,会自动put
                map.merge("key" + (i % 10), 1, Integer::sum);
            }
        };

        Thread t1 = new Thread(computeTask, "ComputeThread");
        Thread t2 = new Thread(mergeTask, "MergeThread-1");
        Thread t3 = new Thread(mergeTask, "MergeThread-2");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("最终结果：");
        map.forEach((k, v) -> System.out.println(k + " = " + v));
    }

}
