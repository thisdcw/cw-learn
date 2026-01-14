package com.cw.design.statemachine.example;


import lombok.Getter;

/**
 * @author thisdcw
 * @date 2026年01月01日 21:55
 */
@Getter
public enum OrderAction {
    DO_PAY,
    DO_CANCEL,
    DO_SHIP,
    DO_RECEIVE,
    DO_REFUND_REQUEST,
    DO_REFUND_APPROVED,
    DO_REFUND_CANCEL,
    DO_COMMENT,
    DO_SHARE,
}
