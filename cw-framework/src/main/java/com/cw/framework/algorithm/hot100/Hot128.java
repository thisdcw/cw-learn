package com.cw.framework.algorithm.hot100;


import java.util.HashSet;
import java.util.Set;

/**
 * @author thisdcw
 * @date 2025年11月20日 21:09
 */
public class Hot128 {

    public static void main(String[] args) {

        int[] nums = {100, 4, 200, 1, 3, 2};
        int[] nums1 = {1, 2, 3, 4, 5, 6};
        System.out.println(longestConsecutive(nums));
        System.out.println(longestConsecutive(nums1));
    }

    public static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }
        int max = 0;
        for (Integer i : set) {
            //找头,必须从头开始
            if (!set.contains(i - 1)) {
                int cur = i;
                int len = 1;
                //当预期值在就+1,直到预期值不在
                while (set.contains(cur + 1)) {
                    cur++;
                    len++;
                }
                //找出当前序列长度与之前序列长度的最大值,即找出最长序列
                max = Math.max(max, len);
            }
        }

        return max;
    }
}
