package com.cw.framework.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author thisdcw
 * @date 2025年11月06日 14:59
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        int parties = 3;
        CyclicBarrier barrier = new CyclicBarrier(parties, () -> {
            System.out.println("所有线程已到达，开始执行汇总任务！");
        });

        for (int i = 0; i < parties; i++) {
            new Thread(new Worker(barrier), "线程-" + i).start();
        }
    }

    static class Worker implements Runnable {
        private final CyclicBarrier barrier;

        Worker(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " 正在执行任务...");
                Thread.sleep((long) (Math.random() * 2000));
                System.out.println(Thread.currentThread().getName() + " 到达屏障点");
                barrier.await();
                System.out.println(Thread.currentThread().getName() + " 继续执行后续任务");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

}
