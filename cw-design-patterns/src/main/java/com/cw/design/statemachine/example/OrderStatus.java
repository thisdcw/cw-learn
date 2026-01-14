package com.cw.design.statemachine.example;

import com.cw.design.statemachine.example.state.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 *
 * @author thisdcw
 * @date 2025年12月31日 13:22
 */
@AllArgsConstructor
@Getter
public enum OrderStatus {
    // 待支付
    UN_PAY(new UnPayState()),

    // 订单已取消
    CANCELLED(null),

    // 待发货
    UN_SHIP(new UnShipState()),

    // 待收货
    UN_RECEIVE(new UnReceiveState()),

    // 已完成
    COMPLETED(new CompleteState()),

    // 申请退款
    REFUND_REQUESTED(new RefundRequestState()),

    // 同意退款
    REFUND_APPROVED(null),

    // 取消退款
    REFUND_CANCELLED(null),

    // 退款完成
    REFUND_COMPLETED(new ConfirmRefundState());

    private final OrderState orderState;
}

