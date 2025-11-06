package com.cw.framework.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author thisdcw
 * @date 2025年11月06日 13:51
 */
public class MultiProducerConsumerExample {
    public static void main(String[] args) {

        // 无限队列
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        // 多生产者
        Runnable producer = () -> {
            for (int i = 0; i < 20; i++) {
                try {
                    String item = Thread.currentThread().getName() + "-Item" + i;
                    queue.put(item); // 队列无限，不会阻塞
                    System.out.println(Thread.currentThread().getName() + " 生产 " + item);
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
            }
        };

        // 多消费者
        Runnable consumer = () -> {
            for (int i = 0; i < 20; i++) {
                try {
                    String item = queue.take(); // 队列空时阻塞
                    System.out.println(Thread.currentThread().getName() + " 消费 " + item);
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }
        };

        // 启动 3 个生产者
        for (int i = 1; i <= 3; i++) {
            new Thread(producer, "Producer-" + i).start();
        }

        // 启动 2 个消费者
        for (int i = 1; i <= 2; i++) {
            new Thread(consumer, "Consumer-" + i).start();
        }
    }
}
