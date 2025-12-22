package com.cw.algorithm.hot100;

/**
 * @author thisdcw
 * @date 2025年12月22日 14:22
 */
public class Hot42 {

    public static void main(String[] args) {

        int[] height = {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};

        System.out.println(trap(height));
    }

    public static int trap(int[] height) {

        int res = 0;
        int[] left = new int[height.length];
        int[] right = new int[height.length];

        for (int i = 1; i < height.length - 1; i++) {
            left[i] = Math.max(left[i - 1], height[i - 1]);
        }

        for (int i = height.length - 2; i >= 0; i--) {
            right[i] = Math.max(right[i + 1], height[i + 1]);
        }

        for (int i = 1; i < height.length - 1; i++) {
            int h = Math.min(left[i], right[i]);
            if (h > height[i]) {
                res = res + (h - height[i]);
            }
        }

        return res;
    }

    /**
     * 双指针
     *
     * @author thisdcw
     * @date 2025/12/22 16:30
     */
    public static int trap2(int[] height) {

        int l = 0, r = height.length - 1;
        int res = 0;
        int leftMax = 0, rightMax = 0;

        while (l < r) {
            leftMax = Math.max(leftMax, height[l]);
            rightMax = Math.max(rightMax, height[r]);

            if (leftMax < rightMax) {
                res = res + leftMax - height[l];
                l++;
            } else {
                res = res + rightMax - height[r];
                r--;
            }
        }

        return res;
    }

    /**
     * 暴力解法
     *
     * @author thisdcw
     * @date 2025/12/22 16:17
     */
    public static int trap1(int[] height) {

        int sum = 0;
        //两边一定不会有水
        for (int i = 1; i < height.length - 1; i++) {

            //求 i 左边的最大值
            int left = 0;
            for (int l = 0; l < i; l++) {
                if (left < height[l]) {
                    left = height[l];
                }
            }
            //求 i 右边的最大值
            int right = 0;
            for (int r = i + 1; r < height.length; r++) {
                if (right < height[r]) {
                    right = height[r];
                }
            }

            //求高度最小值
            int h = Math.min(left, right);
            //求 i 处能存的水,相加
            if (height[i] < h) {
                sum = sum + (h - height[i]);
            }
        }
        return sum;
    }

}
