package com.cw.algorithm.simple;

import java.util.Arrays;

/**
 * @author thisdcw
 * @date 2025年08月26日 15:02
 */
public class LC3000 {

    public static void main(String[] args) {

    }


    public int numOfUnplacedFruits(int[] fruits, int[] baskets) {

        int count = fruits.length;
        Arrays.sort(baskets);
        int basket = 0;

        int[] c = new int[fruits.length];
        for (int i = 0; i < fruits.length; i++) {
            for (int j = basket; j < baskets.length; j++) {
                if (fruits[i] <= baskets[j]) {

                }
            }
        }

        return count;
    }

    public int areaOfMaxDiagonal(int[][] dimensions) {
        long maxDiagonal = 0;
        int maxArea = 0;
        for (int[] dim : dimensions) {
            long diagonal = (long) dim[0] * dim[0] + (long) dim[1] * dim[1];
            int area = dim[0] * dim[1];
            //找对角线最长的
            if (diagonal > maxDiagonal || diagonal == maxDiagonal && area > maxArea) {
                maxDiagonal = diagonal;
                maxArea = area;
            }
        }
        return maxArea;
    }
}
