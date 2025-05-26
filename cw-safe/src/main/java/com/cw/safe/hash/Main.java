package com.cw.safe.hash;

/**
 * @author thisdcw
 * @date 2025年05月26日 10:42
 */
public class Main {
    public static void main(String[] args) {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(3);
        list.add(3);
        list.add(3);
        list.add(3);
        list.add(3);
        list.add(4);
        list.add(4);
        list.add(4);
        list.add(4);
        System.out.println(list.toString());


        list.remove(2);
        System.out.println(list.toString());

        for (Integer i : list) {
            System.out.println(i);
        }
    }
}
