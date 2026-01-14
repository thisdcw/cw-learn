package com.cw.scheduled.core;

/**
 * 定时器接口
 * <p>
 * 定义了定时任务调度的基本操作,包括一次性延迟任务和固定频率周期任务。
 * 这是时间轮定时器的顶层抽象接口,提供了简洁的API供外部使用。
 * </p>
 *
 * @author thisdcw
 * @date 2026年01月14日 10:56
 */
public interface Timer {

    /**
     * 调度一个一次性延迟任务
     * <p>
     * 该任务将在指定的延迟时间后执行一次。
     * 例如: schedule(() -> System.out.println("Hello"), 1000) 表示1秒后打印"Hello"
     * </p>
     *
     * @param task    要执行的任务,以Runnable形式封装
     * @param delayMs 延迟时间(单位:毫秒),必须为正数
     */
    void schedule(Runnable task, long delayMs);

    /**
     * 调度一个固定频率的周期性任务
     * <p>
     * 该任务将以固定的频率周期性执行。
     * 执行频率由periodMs参数决定,与前一次任务的执行时间无关。
     * 例如: scheduleAtFixedRate(() -> System.out.println("Tick"), 1000) 表示每1秒执行一次
     * </p>
     *
     * @param task     要周期性执行的任务
     * @param periodMs 执行周期(单位:毫秒),必须为正数
     */
    void scheduleAtFixedRate(Runnable task, long periodMs);

}
