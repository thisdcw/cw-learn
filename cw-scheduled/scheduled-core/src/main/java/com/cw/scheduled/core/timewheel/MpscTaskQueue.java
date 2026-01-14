package com.cw.scheduled.core.timewheel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MPSC(多生产者单消费者)无锁任务队列
 * <p>
 * <b>核心概念:</b>
 * MPSC = Multiple Producer Single Consumer,即多个生产者可以同时添加任务,但只有一个消费者(Ticker线程)取任务。
 * 这种场景非常适合时间轮的实现:
 * - 生产者: 多个业务线程并发调用addDelayTask添加任务
 * - 消费者: 只有一个Ticker线程负责执行到期任务
 * <p>
 * <b>无锁设计:</b>
 * 使用CAS(Compare And Swap)操作实现无锁并发,避免了synchronized锁的性能开销。
 * 在高并发场景下,无锁算法可以显著提高吞吐量和降低延迟。
 * <p>
 * <b>数据结构:</b>
 * 使用单向链表存储任务,通过AtomicReference保证head指针的原子性更新。
 * 新任务总是添加到链表头部(头插法),这样不需要遍历整个链表,性能更高。
 *
 * @author thisdcw
 * @date 2026年01月14日 14:43
 */
public class MpscTaskQueue {

    /**
     * 链表头指针(使用AtomicReference保证原子性)
     * <p>
     * 使用AtomicReference而不是普通的变量,是因为:
     * 1. 多个线程可能同时修改head指针(并发添加任务)
     * 2. 需要保证head指针的更新是原子操作(要么成功,要么失败)
     * 3. AtomicReference提供了compareAndSet(CAS)方法实现无锁更新
     * <p>
     * <b>初始状态:</b> head = null (队列为空)
     * <b>添加任务后:</b> head指向最新添加的任务
     */
    private final AtomicReference<TimerWheel.DelayTask> head = new AtomicReference<>(null);

    /**
     * 添加任务到队列(无锁算法,使用CAS操作)
     * <p>
     * <b>头插法:</b>
     * 新任务总是插入到链表头部,而不是尾部。这样做的好处是:
     * 1. 不需要维护tail指针,简化实现
     * 2. 不需要遍历整个链表,性能更高(O(1)时间复杂度)
     * <p>
     * <b>CAS操作流程:</b>
     * 1. 读取当前的head指针(oldHead)
     * 2. 将新任务的next指向oldHead
     * 3. 尝试将head从oldHead更新为新任务
     *    - 如果成功:说明没有其他线程干扰,插入完成
     *    - 如果失败:说明有其他线程修改了head,需要重试
     * 4. 失败后重新读取head,重试步骤1-3(自旋)
     * <p>
     * <b>示例:</b>
     * <pre>
     * 初始状态: head -> null
     * 添加任务A: head -> A -> null
     * 添加任务B: head -> B -> A -> null
     * 添加任务C: head -> C -> B -> A -> null
     * </pre>
     *
     * @param delayTask 要添加的延迟任务
     */
    public void pushTask(TimerWheel.DelayTask delayTask) {
        // 自旋CAS操作,直到成功添加任务
        while (true) {
            // 步骤1: 读取当前的head指针
            TimerWheel.DelayTask oldHead = head.get();
            // 步骤2: 将新任务的next指向oldHead(准备插入头部)
            delayTask.next = oldHead;
            // 步骤3: 尝试原子性地将head从oldHead更新为delayTask
            // compareAndSet的语义: 如果当前head等于oldHead,则设置为delayTask并返回true
            // 如果head已经被其他线程修改,则返回false,需要重试
            if (head.compareAndSet(oldHead, delayTask)) {
                // CAS成功,任务添加完成,退出方法
                return;
            }
            // CAS失败,说明有其他线程同时添加了任务,循环重试
        }
    }

    /**
     * 移除并返回所有到期任务
     * <p>
     * <b>执行时机:</b>
     * 当Ticker线程转动到某个槽位时,调用此方法获取该槽位中所有到期的任务。
     * <p>
     * <b>核心逻辑:</b>
     * 遍历链表,判断每个任务是否到期(deadline <= tickTime):
     * 1. 如果未到期: 跳过,继续遍历下一个
     * 2. 如果已到期: 从链表中移除,添加到结果列表
     * <p>
     * <b>CAS操作的复杂性:</b>
     * 移除操作需要特别小心,因为可能有多个线程同时添加任务(head指针可能被修改)。
     * 处理两种情况:
     * 1. 移除非头节点: 直接修改pre.next,不需要CAS
     * 2. 移除头节点: 需要使用CAS原子性地更新head指针
     * <p>
     * <b>并发安全问题:</b>
     * 如果CAS失败(说明head被其他线程修改),需要重新从head开始遍历,
     * 因为链表结构可能已经改变,继续遍历可能导致遗漏或重复。
     *
     * @param tickTime 当前tick的时间戳(用于判断任务是否到期)
     * @return 所有到期任务的 Runnable 列表
     */
    public List<Runnable> removeAndReturnShouldRun(long tickTime) {

        // 结果列表:存储所有到期任务
        List<Runnable> result = new ArrayList<>();
        // 从 head 开始遍历链表
        TimerWheel.DelayTask current = head.get();
        // 记录前一个节点(用于移除操作)
        TimerWheel.DelayTask pre = null;

        // 遍历整个链表
        while (current != null) {
            // 情况1: 任务未到期,跳过
            if (current.deadline > tickTime) {
                // 记录当前节点为pre,继续遍历下一个
                pre = current;
                current = current.next;
                continue;
            }

            // 任务已到期,需要移除
            TimerWheel.DelayTask next = current.next;

            // 情况2: 移除非头节点
            if (pre != null) {
                // 直接修改pre.next,跳过current节点
                pre.next = next;
                // 将任务添加到结果列表
                result.add(current.runnable);
                // 清空next指针,帮助GC
                current.next = null;
                // 继续遍历下一个节点
                current = next;
                continue;
            }

            // 情况3: 移除头节点(需要CAS操作)
            // 尝试原子性地将head从current更新为next
            if (head.compareAndSet(current, next)) {
                // CAS成功,head已更新
                result.add(current.runnable);
                current.next = null;
                current = next;
                continue;
            }

            // CAS失败,说明有其他线程修改了head
            // 需要重新从head开始遍历(因为链表结构可能已改变)
            current = head.get();
            // 重置 pre 为null
            pre = null;

        }
        // 返回所有到期任务
        return result;
    }

}
