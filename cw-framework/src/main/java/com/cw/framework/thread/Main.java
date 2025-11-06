package com.cw.framework.thread;

/**
 * @author thisdcw
 * @date 2025年10月30日 10:16
 */
public class Main {

    public int score;

    public synchronized void addScore() {
        score += 1;
    }

    public static void main(String[] args) throws InterruptedException {

//       MyThread thread = new MyThread();
//       thread.start();

//       Thread thread1 = new Thread(new MyRunnable());
//        System.out.println(thread1.getState());
//        thread1.start();
//        System.out.println(thread1.getState());

        Runnable task = () -> {
            for (int i = 0; i < 100000; i++) {
                Singleton s = Singleton.getInstance();
                // 如果看到半初始化对象
                if (s.x != 42) {
                    System.out.println("发现半初始化对象: " + s.x);
                    break;
                }
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();



    }
}
