package com.cw.algorithm.middle;


import com.cw.algorithm.ListNode;

import java.util.HashSet;
import java.util.Set;

/**
 * @author thisdcw
 * @date 2025年11月01日 16:46
 */
public class LC3217 {

    public static void main(String[] args) {

        ListNode node = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5)))));

        ListNode listNode = modifiedList(new int[]{1, 2, 3}, node);
        System.out.println(listNode);
    }

    public static ListNode modifiedList(int[] nums, ListNode head) {
        Set<Integer> set = new HashSet<>();
        for (int x : nums) {
            set.add(x);
        }
        return dfs(head, set);
    }

    private static ListNode dfs(ListNode head, Set<Integer> set) {
        if (head == null) {
            return null;
        }
        head.setNext(dfs(head.getNext(), set));
        return set.contains(head.getVal()) ? head.getNext() : head;
    }

}
