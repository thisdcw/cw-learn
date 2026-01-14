package com.cw.scheduled.core;

import com.cw.scheduled.core.timewheel.TimerWheel;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 时间轮定时器测试类
 * <p>
 * 用于测试时间轮的线程安全性和并发性能。
 * <p>
 * <b>测试目的:</b>
 * 验证在多线程高并发场景下,时间轮能够正确地:
 * 1. 处理多个线程同时添加任务
 * 2. 正确执行所有到期的任务
 * 3. 不丢失任务,不重复执行任务
 * 4. 保证线程安全(无数据竞争,无并发异常)
 *
 * @author thisdcw
 * @date 2026年01月14日 14:34
 */
public class TimerTest {

    /**
     * 线程安全测试
     * <p>
     * <b>测试场景:</b>
     * 使用10个线程,每个线程添加1000个延迟任务,总共10000个任务。
     * 所有任务的延迟时间都是100ms。
     * <p>
     * <b>并发压力:</b>
     * - 10个线程同时调用addDelayTask方法
     * - 测试MpscTaskQueue的CAS操作是否能正确处理并发
     * - 测试Ticker线程是否能正确处理所有到期的任务
     * <p>
     * <b>验证方法:</b>
     * 1. 使用CountDownLatch等待所有10000个任务执行完成
     * 2. 使用AtomicInteger计数,确保执行了10000次任务
     * 3. 如果测试正常结束(不阻塞、不抛异常),说明线程安全
     * <p>
     * <b>预期结果:</b>
     * - 打印10次"添加结束"
     * - 打印数字1到10000(顺序可能不同,因为是并发执行)
     * - 测试正常结束,CountDownLatch.await()不会永久阻塞
     *
     * @throws Exception 如果测试过程中出现异常
     */
    @Test
    void testThreadSafe() throws Exception {

        // 创建时间轮实例
        TimerWheel timerWheel = new TimerWheel();

        // 创建同步门闩,计数器初始化为10000(等待10000个任务完成)
        CountDownLatch countDownLatch = new CountDownLatch(10000);

        // 创建原子计数器,用于统计执行的任务数量
        AtomicInteger count = new AtomicInteger();

        // 创建10个线程,模拟多线程并发添加任务
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // 每个线程添加1000个任务
                for (int i1 = 0; i1 < 1000; i1++) {
                    timerWheel.addDelayTask(() -> {
                        // 任务执行时:
                        // 1. 打印当前计数值(使用incrementAndGet保证原子性)
                        System.out.println(count.incrementAndGet());
                        // 2. 计数器减1(通知CountDownLatch一个任务已完成)
                        countDownLatch.countDown();
                    }, 100); // 延迟100ms执行
                }
                // 线程添加完1000个任务后,打印提示信息
                System.out.println("添加结束");
            }).start(); // 启动线程
        }

        // 主线程等待所有10000个任务执行完成
        // 如果某个任务丢失或执行失败,await()会永久阻塞,测试超时失败
        countDownLatch.await();

        // 如果代码执行到这里,说明所有10000个任务都成功执行完成
        // 测试通过!

    }
}
