package com.cw.framework.thread;

import java.util.concurrent.locks.StampedLock;

/**
 * @author thisdcw
 * @date 2025年11月05日 15:57
 */
public class StampedLockDemo {

    private final StampedLock lock = new StampedLock();
    private int data = 0;

    // 乐观读
    public void optimisticRead() {
        // 乐观读，无锁
        long stamp = lock.tryOptimisticRead();
        int value = data;
        try {
            // 模拟读耗时
            Thread.sleep(300);
        } catch (InterruptedException ignored) {}

        // 校验读期间是否有写锁发生
        if (!lock.validate(stamp)) {
            // 失败则升级为悲观读
            stamp = lock.readLock();
            try {
                value = data;
                System.out.println(Thread.currentThread().getName() + " 乐观读失败，升级悲观读 data=" + value);
            } finally {
                lock.unlockRead(stamp);
            }
        } else {
            System.out.println(Thread.currentThread().getName() + " 乐观读成功 data=" + value);
        }
    }

    // 悲观读
    public void read() {
        long stamp = lock.readLock();
        try {
            System.out.println(Thread.currentThread().getName() + " 悲观读 data=" + data);
            Thread.sleep(300);
        } catch (InterruptedException ignored) {
        } finally {
            lock.unlockRead(stamp);
        }
    }

    // 写
    public void write() {
        long stamp = lock.writeLock();
        try {
            data++;
            System.out.println(Thread.currentThread().getName() + " 写入 data=" + data);
            Thread.sleep(300);
        } catch (InterruptedException ignored) {
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public static void main(String[] args) {
        StampedLockDemo demo = new StampedLockDemo();

        // 乐观读线程
        for (int i = 0; i < 3; i++) {
            new Thread(demo::optimisticRead, "乐观读线程-" + i).start();
        }

        // 写线程（让读有概率失败）
        new Thread(() -> {
            try { Thread.sleep(50); } catch (Exception ignored) {}
            demo.write();
        }, "写线程").start();

        // 悲观读线程
        new Thread(demo::read, "悲观读线程").start();
    }

}
