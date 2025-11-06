package com.cw.framework.thread;

import java.util.concurrent.*;

/**
 * @author thisdcw
 * @date 2025年11月06日 13:56
 */
public class ThreadPoolCoreConfigExample {

    public static void main(String[] args) {

        // 核心参数配置
        // 核心线程数
        int corePoolSize = 4;
        // 最大线程数
        int maximumPoolSize = 8;
        // 非核心线程空闲存活时间
        long keepAliveTime = 30;
        TimeUnit unit = TimeUnit.SECONDS;
        // 阻塞队列容量
        int queueCapacity = 100;

        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(queueCapacity);

        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "CustomPool-Thread-" + count++);
                t.setDaemon(false);
                return t;
            }
        };

        RejectedExecutionHandler rejectedHandler = new ThreadPoolExecutor.CallerRunsPolicy();

        // 创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                rejectedHandler
        );

        // 提交任务示例
        for (int i = 1; i <= 20; i++) {
            final int taskNum = i;
            executor.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " 执行任务 " + taskNum);
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            });
        }

        executor.shutdown();
    }

}
