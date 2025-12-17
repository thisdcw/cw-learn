package com.cw.lab.thread;

import java.util.concurrent.*;

/**
 * @author thisdcw
 * @date 2025年11月06日 13:58
 */
public class RejectedPolicyExample {

    public static void main(String[] args) {

        int corePoolSize = 1;
        int maxPoolSize = 2;
        int queueCapacity = 2;

        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(queueCapacity);

        // 可以切换不同的拒绝策略测试效果
//        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
//         RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
//         RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                10,
                TimeUnit.SECONDS,
                workQueue,
                Executors.defaultThreadFactory(),
                handler
        );

        // 提交 6 个任务
        for (int i = 1; i <= 6; i++) {
            final int taskNum = i;
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 执行任务 " + taskNum);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
            });
        }

        executor.shutdown();
    }

}
