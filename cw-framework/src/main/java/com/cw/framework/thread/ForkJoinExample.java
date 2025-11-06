package com.cw.framework.thread;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @author thisdcw
 * @date 2025年11月06日 14:08
 */
public class ForkJoinExample {

    // 任务类：递归分治求和
    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000; // 拆分阈值
        private final long start;
        private final long end;

        public SumTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            // 小任务，直接计算
            if ((end - start) <= THRESHOLD) {
                long sum = 0;
                for (long i = start; i <= end; i++) {
                    sum += i;
                }
                return sum;
            }
            // 大任务，分治拆分
            long middle = (start + end) / 2;
            SumTask left = new SumTask(start, middle);
            SumTask right = new SumTask(middle + 1, end);

            // 异步执行子任务
            left.fork();
            right.fork();

            // 合并结果
            return left.join() + right.join();
        }
    }

    public static void main(String[] args) {
        long start = 1;
        long end = 100_000_000;

        // 默认并行度 = CPU核心数
        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        long result = pool.invoke(new SumTask(start, end));
        long endTime = System.currentTimeMillis();

        System.out.println("结果: " + result);
        System.out.println("耗时: " + (endTime - startTime) + " ms");

        pool.shutdown();
    }

}
