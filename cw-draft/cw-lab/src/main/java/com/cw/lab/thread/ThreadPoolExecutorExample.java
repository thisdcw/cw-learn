package com.cw.lab.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author thisdcw
 * @date 2025年11月06日 13:53
 */
public class ThreadPoolExecutorExample {
    public static void main(String[] args) {

        // 核心线程数 2，最大线程数 4，空闲线程存活时间 60 秒
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,
                4,
                60,
                TimeUnit.SECONDS,
                // 队列容量 10
                new LinkedBlockingQueue<>(10),
                new ThreadFactory() {
                    // 自定义线程名
                    private int count = 1;
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "Worker-" + count++);
                    }
                },
                // 队列满时使用调用线程执行
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 提交 15 个任务
        for (int i = 1; i <= 15; i++) {
            int taskNum = i;
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 执行任务 " + taskNum);
                try {
                    Thread.sleep(500); // 模拟任务耗时
                } catch (InterruptedException ignored) {}
            });
        }

        executor.shutdown(); // 优雅关闭线程池
    }
}
