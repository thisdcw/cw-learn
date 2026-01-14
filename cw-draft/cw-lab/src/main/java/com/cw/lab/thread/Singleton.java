package com.cw.lab.thread;

/**
 * @author thisdcw
 * @date 2025年11月05日 13:11
 */
class Singleton {
    // 不加 volatile 可能触发半初始化
   volatile static Singleton instance;

    int x;

    // 初始化
    private Singleton() {
        x = 42;
    }

    static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

