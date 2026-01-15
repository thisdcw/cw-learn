package com.cw.scheduled.core.timewheel;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * 时间轮定时器核心实现类
 * <p>
 * 时间轮(Time Wheel)是一种高效的定时任务调度算法,广泛应用于Netty、Kafka等高性能框架中。
 * <p>
 * <b>核心思想:</b>
 * 将时间划分为多个固定的"槽"(slot),每个槽代表一个时间间隔(称为tick)。
 * 通过一个指针按照固定频率转动,指向不同的槽,执行该槽中的到期任务。
 * <p>
 * <b>优势:</b>
 * 1. 添加任务的时间复杂度为O(1),与任务数量无关
 * 2. 不需要像PriorityQueue那样每次都要排序
 * 3. 特别适合管理大量短延时的定时任务
 * <p>
 * <b>实现细节:</b>
 * - 使用环形数组作为时间轮,大小为10个槽
 * - 每个槽代表100ms的时间间隔
 * - 使用CAS操作实现无锁并发,保证线程安全
 * - 使用独立的线程池执行到期任务,避免阻塞时间轮转动
 *
 * @author thisdcw
 * @date 2026年01月14日 13:29
 */
public class TimerWheel {

    /**
     * 时间轮启动时间戳(毫秒)
     * 使用volatile保证可见性,多线程环境下能及时读取到最新的启动时间
     */
    private volatile long startTime;

    /**
     * 时间轮数组(环形队列)
     * 每个元素是一个MPSC(多生产者单消费者)队列,存储该槽位的延迟任务
     * 数组大小为10,表示时间轮有10个槽
     */
    private final MpscTaskQueue[] wheel;

    /**
     * 时间轮驱动线程
     * 负责按固定频率转动时间轮,执行到期任务
     */
    private final Ticker ticker;

    /**
     * 时间轮启动状态标志
     * 使用AtomicBoolean实现原子操作,保证只启动一次
     * false-未启动, true-已启动
     */
    private final AtomicBoolean started;

    /**
     * 启动时间同步门闩
     * 用于确保startTime初始化完成后再允许添加任务
     * 避免在startTime未初始化时就计算任务槽位,导致槽位计算错误
     */
    private final CountDownLatch startTimeLatch;

    /**
     * 任务执行线程池
     * 固定大小为6个线程,负责执行到期的定时任务
     * 使用独立线程池可以避免任务执行阻塞时间轮的转动
     */
    private final ExecutorService executor;

    /**
     * 构造函数
     * 初始化时间轮的所有核心组件:
     * 1. 创建10个槽的时间轮数组
     * 2. 初始化驱动线程、状态标志、同步门闩
     * 3. 创建任务执行线程池(6个线程)
     */
    public TimerWheel() {
        // 创建包含10个槽的时间轮数组
        this.wheel = new MpscTaskQueue[10];
        // 创建时间轮驱动线程
        this.ticker = new Ticker();
        // 初始化启动状态为false
        this.started = new AtomicBoolean(false);
        // 初始化同步门闩,计数为1(需要等待startTime初始化完成)
        this.startTimeLatch = new CountDownLatch(1);
        // 创建固定大小为6的任务执行线程池
        this.executor = Executors.newFixedThreadPool(6);

        // 初始化每个槽,创建MPSC队列实例
        for (int i = 0; i < wheel.length; i++) {
            wheel[i] = new MpscTaskQueue();
        }
    }

    /**
     * 添加一个延迟任务到时间轮
     * <p>
     * <b>执行流程:</b>
     * 1. 确保时间轮已启动(首次调用时启动)
     * 2. 创建延迟任务对象,计算任务的到期时间戳
     * 3. 根据延迟时间计算任务应该放入哪个槽位
     * 4. 将任务添加到对应槽位的MPSC队列中
     * <p>
     * <b>槽位计算公式:</b>
     * index = ((任务到期时间 - 启动时间) / tick间隔) % 槽位数
     * 例如: 启动后150ms添加一个延迟100ms的任务
     * - 到期时间 = 启动时间 + 150ms + 100ms = 启动时间 + 250ms
     * - index = (250ms / 100ms) % 10 = 2 % 10 = 2
     * - 任务放入第2号槽位
     *
     * @param task    要执行的任务
     * @param delayMs 延迟时间(毫秒)
     */
    public void addDelayTask(Runnable task, long delayMs) {
        // 确保时间轮已启动(如果还未启动则启动)
        start();
        // 创建延迟任务对象,内部会计算deadline = 当前时间 + 延迟时间
        DelayTask delayTask = new DelayTask(task, delayMs);
        // 计算任务应该放入哪个槽位:
        // (deadline - startTime): 距离启动的时间差
        // / 100: 除以tick间隔(100ms),得到第几个tick
        // % wheel.length: 对槽位数取模,得到数组下标
        int index = Math.toIntExact(((delayTask.deadline - startTime) / 100) % wheel.length);
        // 获取对应槽位的队列
        MpscTaskQueue slot = wheel[index];
        // 将任务无锁地添加到队列中(CAS操作)
        slot.pushTask(delayTask);
    }

    /**
     * 启动时间轮(懒加载模式)
     * <p>
     * 使用CAS(Compare And Swap)操作保证时间轮只启动一次,即使多线程同时调用也不会重复启动。
     * <p>
     * <b>执行流程:</b>
     * 1. 使用CAS原子操作将started状态从false设置为true
     * 2. 如果设置成功(说明是首次启动),则启动Ticker驱动线程
     * 3. 等待startTime初始化完成(Ticker线程启动后会初始化startTime)
     * <p>
     * <b>为什么使用CountDownLatch?</b>
     * startTime是在Ticker线程中初始化的,而addDelayTask方法需要使用startTime来计算槽位。
     * 如果不等待startTime初始化完成,可能会导致计算错误或空指针异常。
     * CountDownLatch确保startTime初始化完成后,才允许添加任务。
     */
    private void start() {
        // CAS操作:如果当前状态是false,则设置为true并返回true
        // 只有首次调用时才会进入if块启动线程
        if (started.compareAndSet(false, true)) {
            // 启动时间轮驱动线程
            ticker.start();
        }
        try {
            // 等待startTime初始化完成(Ticker线程中会调用countDown)
            startTimeLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止时间轮
     * <p>
     * 使用CAS操作将started状态从true设置为false,并唤醒Ticker线程使其退出循环。
     * <p>
     * <b>执行流程:</b>
     * 1. 使用CAS操作将状态从true改为false(只有已启动的时间轮才能停止)
     * 2. 唤醒Ticker线程(Ticker线程在循环中会检查started状态,如果为false则退出)
     */
    public void stop() {
        // CAS操作:如果当前状态是true,则设置为false并返回true
        // 只有已启动的时间轮才能停止
        if (started.compareAndSet(true, false)) {
            // 唤醒Ticker线程(如果它正在park等待)
            LockSupport.unpark(ticker);
        }
    }

    /**
     * 延迟任务内部类
     * <p>
     * 封装了延迟任务的核心信息,包括要执行的任务、到期时间、以及前后节点的引用。
     * 采用双向链表结构(虽然当前只使用了单向链表的部分功能),便于任务的管理和删除。
     * <p>
     * <b>设计说明:</b>
     * - runnable: 要执行的实际任务,封装为Runnable接口
     * - deadline: 任务的到期时间戳(毫秒),用于判断任务是否到期
     * - next/pre: 双向链表指针,用于构建任务链表(当前实现中使用较少)
     */
    public class DelayTask {

        /**
         * 要执行的任务
         * 用户的实际业务逻辑封装在这里
         */
        final Runnable runnable;

        /**
         * 任务到期时间戳(毫秒)
         * deadline = 当前时间 + 延迟时间
         * 例如: 当前时间1000ms,延迟500ms,则deadline=1500ms
         */
        final long deadline;

        /**
         * 后继节点指针
         * 指向链表中的下一个任务节点
         */
        DelayTask next;

        /**
         * 前驱节点指针
         * 指向链表中的上一个任务节点
         * (当前实现中未充分利用,预留用于双向链表操作)
         */
        DelayTask pre;

        /**
         * 构造延迟任务
         *
         * @param runnable 要执行的任务
         * @param delayMs  延迟时间(毫秒)
         */
        public DelayTask(Runnable runnable, long delayMs) {
            this.runnable = runnable;
            // 计算任务到期时间戳 = 当前时间 + 延迟时间
            this.deadline = System.currentTimeMillis() + delayMs;
        }

    }

    /**
     * 时间槽内部类(已弃用,当前版本使用MpscTaskQueue替代)
     * <p>
     * <b>历史说明:</b>
     * 这是早期版本的时间槽实现,使用双向链表存储任务,并通过synchronized保证线程安全。
     * 但由于锁竞争会影响性能,后来被无锁的MpscTaskQueue替代。
     * 保留此代码是为了学习参考,展示了基于链表和锁的槽位实现方式。
     * <p>
     * <b>设计说明:</b>
     * - head/tail: 双向链表的头尾指针
     * - pushDelayTask: 添加任务到链表尾部
     * - runWithDeadLine: 执行到期的任务
     * - removeTask: 从链表中删除节点
     */
    @Deprecated
    public class Slot {

        /**
         * 链表头指针
         * 指向槽位中的第一个任务
         */
        DelayTask head;

        /**
         * 链表尾指针
         * 指向槽位中的最后一个任务
         */
        DelayTask tail;

        /**
         * 添加任务到槽位(线程安全,使用synchronized锁)
         * 将新任务添加到链表尾部
         *
         * @param delayTask 要添加的延迟任务
         */
        private synchronized void pushDelayTask(DelayTask delayTask) {
            // 如果链表为空,新任务既是头也是尾
            if (head == null) {
                head = tail = delayTask;
            } else {
                // 否则将新任务添加到尾部
                tail.next = delayTask;      // 尾节点的 next 指向新任务
                delayTask.pre = tail;       // 新任务的 pre 指向尾节点
                tail = delayTask;           // 更新尾指针
            }
        }

        /**
         * 执行到期的任务
         * 遍历链表,找到所有deadline <= tickTime的任务并执行
         *
         * @param tickTime 当前tick的时间戳
         */
        private synchronized void runWithDeadLine(long tickTime) {
            DelayTask current = head;
            while (current != null) {
                DelayTask next = current.next;
                // 如果任务到期(deadline <= 当前tick时间)
                if (current.deadline <= tickTime) {
                    // 从链表中移除该任务
                    removeTask(current);
                    // 提交到线程池执行
                    executor.execute(current.runnable);
                }
                current = next;
            }

        }

        /**
         * 从双向链表中删除指定节点
         * 需要处理四种情况:
         * 1. 删除中间节点
         * 2. 删除头节点
         * 3. 删除尾节点
         * 4. 删除唯一节点
         *
         * @param current 要删除的任务节点
         */
        private void removeTask(DelayTask current) {
            // 处理前驱节点
            if (current.pre != null) {
                current.pre.next = current.next;
            }
            // 处理后继节点
            if (current.next != null) {
                current.next.pre = current.pre;
            }
            // 如果是头节点,更新head指针
            if (current == head) {
                head = current.next;
            }
            // 如果是尾节点,更新tail指针
            if (current == tail) {
                tail = current.pre;
            }

            // 清空节点的前后指针,帮助GC
            current.pre = null;
            current.next = null;
        }

    }

    /**
     * 时间轮驱动线程内部类
     * <p>
     * 这是时间轮的核心驱动引擎,负责按固定间隔(100ms)转动时间轮,执行到期的任务。
     * <p>
     * <b>工作原理:</b>
     * 1. 初始化startTime并通知等待线程
     * 2. 循环执行:
     *    a) 计算下一个tick的时间点
     *    b) 使用LockSupport.parkUntil精确等待到该时间点(比sleep更高效)
     *    c) 检查started状态,如果已停止则退出
     *    d) 计算当前应该处理哪个槽位
     *    e) 从槽位中取出所有到期任务
     *    f) 提交到线程池执行
     *    g) tick计数器递增
     * <p>
     * <b>时间轮转动示例:</b>
     * <pre>
     * tick=0: 处理槽位0 (0-100ms的任务)
     * tick=1: 处理槽位1 (100-200ms的任务)
     * tick=2: 处理槽位2 (200-300ms的任务)
     * ...
     * tick=9: 处理槽位9 (900-1000ms的任务)
     * tick=10: 处理槽位0 (1000-1100ms的任务) - 循环回到槽位0
     * </pre>
     */
    public class Ticker extends Thread {

        /**
         * tick计数器
         * 记录时间轮转动的次数,用于计算当前应该处理哪个槽位
         */
        int tickCount = 0;

        /**
         * 线程运行主方法
         * 驱动时间轮持续转动,执行到期任务
         */
        @Override
        public void run() {

            // 初始化启动时间戳(必须最先执行,因为其他线程依赖这个值)
            startTime = System.currentTimeMillis();
            // 通知等待线程(startTime已初始化完成)
            startTimeLatch.countDown();

            // 主循环:时间轮持续转动
            while (started.get()) {
                // 计算下一个tick的准确时间点
                // 例如: tickCount=0时, tickTime=startTime+100 (第一个tick在100ms时触发)
                long tickTime = startTime + (tickCount + 1) * 100L;

                // 精确等待到tick时间点
                // LockSupport.parkUntil比Thread.sleep更高效,可以被unpark唤醒
                while (tickTime >= System.currentTimeMillis()) {
                    LockSupport.parkUntil(tickTime);
                    // 检查是否被停止(如果stop()方法被调用,started会变成false)
                    if (!started.get()) {
                        return;
                    }
                }

                // 计算当前应该处理哪个槽位(使用取模实现环形数组)
                // 例如: 10个槽, tickCount=10时, index=0 (回到第一个槽)
                int index = tickCount % wheel.length;
                // 获取该槽位的任务队列
                MpscTaskQueue slot = wheel[index];
                // 从队列中移除所有到期任务,并返回要执行的任务列表
                List<Runnable> runnables = slot.removeAndReturnShouldRun(tickTime);
                // 将所有到期任务提交到线程池执行
                runnables.forEach(executor::execute);
                // tick计数器递增,准备处理下一个槽位
                tickCount++;
            }
        }
    }

}
