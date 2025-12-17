package com.cw.lab.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author thisdcw
 * @date 2025年11月06日 14:41
 */
public class CountDownLatchExample {

    public static void main(String[] args) throws InterruptedException {
        int taskCount = 3;
        CountDownLatch latch = new CountDownLatch(taskCount);
        ExecutorService executor = Executors.newFixedThreadPool(taskCount);

        for (int i = 0; i < taskCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    System.out.println("任务 " + index + " 开始执行");
                    Thread.sleep(1000 + index * 500);
                    System.out.println("任务 " + index + " 执行完成");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 每完成一个任务，计数减1
                    latch.countDown();
                }
            });
        }

        System.out.println("主线程等待所有任务完成...");
        latch.await(); // 阻塞，直到 countDown 次数为 0
        System.out.println("所有任务已完成，主线程继续执行");

        executor.shutdown();
    }

}
