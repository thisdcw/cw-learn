package com.cw.framework.thread;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author thisdcw
 * @date 2025年11月05日 15:36
 */
public class ReadWriteLockDemo {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private int data = 0;

    public void read() {
        rwLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 正在读取 data=" + data);
            // 模拟读取耗时
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void write() {
        rwLock.writeLock().lock();
        try {
            data++;
            System.out.println(Thread.currentThread().getName() + " 正在写入 data=" + data);
            // 模拟写入耗时
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static void main(String[] args) {
        ReadWriteLockDemo demo = new ReadWriteLockDemo();

        // 多读线程
        for (int i = 0; i < 3; i++) {
            new Thread(demo::read, "读线程-" + i).start();
        }

        // 写线程
        new Thread(demo::write, "写线程-1").start();

        // 再创建读线程，看看写锁期间读锁是否阻塞
        new Thread(demo::read, "读线程-延迟").start();

    }

}
