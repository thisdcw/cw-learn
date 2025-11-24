package com.cw.framework.algorithm.hot100;


import java.util.Arrays;
import java.util.HashMap;

/**
 * @author thisdcw
 * @date 2025年11月21日 20:57
 */
public class Hot1 {

    public static void main(String[] args) {

        int[] nums = {2, 7, 11, 15};
        int[] nums1 = {3, 3};

        System.out.println(Arrays.toString(twoSum(nums, 9)));
        System.out.println(Arrays.toString(twoSum(nums1, 6)));
    }

    public static int[] twoSum(int[] nums, int target) {


//        for (int i = 0; i < nums.length; i++) {
//            for (int i1 = nums.length - 1; i1 > i; i1--) {
//                if (nums[i] + nums[i1] == target) {
//                    return new int[]{i, i1};
//                }
//            }
//        }
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                return new int[]{map.get(target - nums[i]), i};
            }
            map.put(nums[i], i);
        }
        return new int[0];
    }
}
