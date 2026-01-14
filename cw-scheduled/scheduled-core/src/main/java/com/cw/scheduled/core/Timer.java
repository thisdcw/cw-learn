package com.cw.scheduled.core;

/**
 * @author thisdcw
 * @date 2026年01月14日 10:56
 */
public interface Timer {

    void schedule(Runnable task, long delayMs);

    void scheduleAtFixedRate(Runnable task, long periodMs);

}
