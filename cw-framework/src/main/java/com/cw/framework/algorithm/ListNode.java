package com.cw.framework.algorithm;


import lombok.Data;
import lombok.ToString;

/**
 * @author thisdcw
 * @date 2025年11月01日 16:47
 */
@ToString
@Data
public class ListNode {

    int val;
    ListNode next;

    public ListNode() {
    }

    public ListNode(int val) {
        this.val = val;
    }

    public ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}
