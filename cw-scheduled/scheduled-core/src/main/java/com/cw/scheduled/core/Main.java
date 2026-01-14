package com.cw.scheduled.core;

import com.cw.scheduled.core.timewheel.TimerWheel;

/**
 * 时间轮定时器使用示例
 * <p>
 * 这个类展示了如何使用TimerWheel时间轮定时器来调度延迟任务。
 * <p>
 * <b>示例说明:</b>
 * 创建100个延迟任务,每个任务的延迟时间递增(0秒,1秒,2秒,...,99秒)。
 * 这些任务会在各自指定的延迟时间后打印出任务的序号。
 * <p>
 * <b>预期输出:</b>
 * 程序会先打印"添加结束",然后每隔1秒打印一个数字,从0到99。
 * (注意: 由于时间轮是10个槽,每个槽代表100ms,任务会按照到期时间执行)
 *
 * @author thisdcw
 * @date 2026年01月14日 10:16
 */
public class Main {

    /**
     * 主函数:演示时间轮定时器的基本用法
     *
     * @param args 命令行参数(未使用)
     */
    public static void main(String[] args) {

        // 步骤1: 创建时间轮实例
        // 内部会初始化10个槽的时间轮数组、Ticker驱动线程、任务执行线程池等
        TimerWheel timerWheel = new TimerWheel();

        // 步骤2: 添加100个延迟任务
        for (int i = 0; i < 100; i++) {
            // 使用final变量捕获循环变量( Lambda表达式中要求捕获的变量是final或effectively final)
            final int fi = i;

            // 添加延迟任务:
            // - 任务内容: 打印当前序号fi
            // - 延迟时间: 1000 * i 毫秒(即第i个任务延迟i秒)
            // 例如: i=0时延迟0秒(立即执行), i=1时延迟1秒, i=99时延迟99秒
            timerWheel.addDelayTask(() -> {
                System.out.println(fi);
            }, 1000 * i);
        }

        // 注意: 主线程添加完任务后就结束了
        // 但时间轮的Ticker线程和任务执行线程池会继续运行,直到所有任务执行完成

        // 如果需要等待所有任务执行完成,可以添加:
        // try { Thread.sleep(100000); } catch (InterruptedException e) { e.printStackTrace(); }

    }

}
