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

        Main main = new Main();

        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                main.addScore();
            }).start();
        }

        Thread.sleep(10000L);
        System.out.println(main.score);

    }
}
