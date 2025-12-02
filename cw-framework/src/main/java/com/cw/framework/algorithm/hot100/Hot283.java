package com.cw.framework.algorithm.hot100;


import java.util.Arrays;

/**
 * @author thisdcw
 * @date 2025年11月24日 21:15
 */
public class Hot283 {

    public static void main(String[] args) {

        int[] nums = {0, 1, 0, 3, 12};
        moveZeroes(nums);
        System.out.println(Arrays.toString(nums));
    }

    public static void moveZeroes(int[] nums) {

        int j = 0;
        for(int i=0;i<nums.length;i++) {
            //nums[i]如果不等于0,那么i和j会一起自增,此时i的位置不一定和j一样,但是j只会有两种情况,等于0或者等于i
            //nums[i]如果等于0,那么j会记录当前位置
            if(nums[i]!=0) {
                if (i>j){
                    nums[j] = nums[i];
                    nums[i] = 0;
                }
                j++;
            }
        }
    }
}
