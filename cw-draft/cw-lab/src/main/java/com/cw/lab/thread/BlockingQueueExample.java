package com.cw.lab.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author thisdcw
 * @date 2025年11月06日 13:16
 */
public class BlockingQueueExample {

    public static void main(String[] args) {
        // 容量为5的阻塞队列
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);

        // 生产者
        Runnable producer = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    String item = "Item-" + i;
                    queue.put(item); // 队列满时阻塞
                    System.out.println(Thread.currentThread().getName() + " 生产 " + item);
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }
        };

        // 消费者
        Runnable consumer = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    // 队列空时阻塞
                    String item = queue.take();
                    System.out.println(Thread.currentThread().getName() + " 消费 " + item);
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {}
            }
        };

        Thread producerThread = new Thread(producer, "Producer");
        Thread consumerThread = new Thread(consumer, "Consumer");

        producerThread.start();
        consumerThread.start();
    }

}
