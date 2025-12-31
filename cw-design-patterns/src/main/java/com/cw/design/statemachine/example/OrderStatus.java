package com.cw.design.statemachine.example;

/**
 * @author thisdcw
 * @date 2025年12月31日 13:22
 */
public enum OrderStatus {
    // 待支付
    UN_PAY,

    // 订单已取消
    CANCELLED,

    // 待发货
    UN_SHIP,

    // 待收货
    UN_RECEIVE,

    // 已完成
    COMPLETED,

    // 申请退款
    REFUND_REQUESTED,

    // 同意退款
    REFUND_APPROVED,

    // 取消退款
    REFUND_CANCELLED,

    // 退款完成
    REFUND_COMPLETED;
}

