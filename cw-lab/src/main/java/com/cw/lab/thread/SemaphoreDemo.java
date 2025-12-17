package com.cw.lab.thread;

import java.util.concurrent.Semaphore;

/**
 * @author thisdcw
 * @date 2025年11月06日 15:07
 */
public class SemaphoreDemo {

    public static void main(String[] args) {
        // 同时允许3个线程访问
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 10; i++) {
            new Thread(new Worker(semaphore), "线程-" + i).start();
        }
    }

    static class Worker implements Runnable {
        private final Semaphore semaphore;

        Worker(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire(); // 获取许可
                System.out.println(Thread.currentThread().getName() + " 获取到许可，正在执行任务");
                Thread.sleep((long) (Math.random() * 2000));
                System.out.println(Thread.currentThread().getName() + " 任务完成，释放许可");
                semaphore.release(); // 释放许可
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
