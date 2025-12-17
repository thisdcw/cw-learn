package com.cw.lab.thread;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author thisdcw
 * @date 2025年11月05日 14:41
 */
public class ReentrantLockDemo {

    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;

    // 基本加锁示例
    public void increment() {
        // 获取锁
        lock.lock();
        try {
            count++;
            System.out.println(Thread.currentThread().getName() + " count=" + count);
        } finally {
            // 必须在 finally 中释放锁
            lock.unlock();
        }
    }

    // 可重入示例
    public void outer() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " enter outer");
            inner();
        } finally {
            lock.unlock();
        }
    }

    private void inner() {
        // 同一线程可以再次获取锁
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " enter inner");
        } finally {
            lock.unlock();
        }
    }

    // 尝试锁示例
    public void tryLockExample() {
        // 尝试获取锁，获取不到不会阻塞
        if (lock.tryLock()) {
            try {
                System.out.println(Thread.currentThread().getName() + " acquired lock via tryLock");
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println(Thread.currentThread().getName() + " failed to acquire lock");
        }
    }

    public static void main(String[] args) {
        ReentrantLockDemo demo = new ReentrantLockDemo();

        // 启动多个线程
        for (int i = 0; i < 3; i++) {
            new Thread(demo::increment, "Thread-" + i).start();
        }

        // 可重入锁演示
        new Thread(demo::outer, "ReentrantThread").start();

        // 尝试锁演示
        new Thread(demo::tryLockExample, "TryLockThread").start();
    }

}
