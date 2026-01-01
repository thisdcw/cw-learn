package com.cw.design.statemachine.example.state;


import com.cw.design.statemachine.example.OrderContext;
import com.cw.design.statemachine.example.OrderState;
import com.cw.design.statemachine.example.OrderStatus;

/**
 * 订单已完成允许的操作
 *
 * @author thisdcw
 * @date 2026年01月01日 20:32
 */
public class CompleteState implements OrderState {

    @Override
    public void share(OrderContext ctx) {
        System.out.println("完成订单 - 分享订单");
    }

    @Override
    public void comment(OrderContext ctx) {
        System.out.println("完成订单 - 评论");
    }

    @Override
    public void applyRefund(OrderContext ctx) {
        System.out.println("申请退款");
        ctx.setOrderState(new UnConfirmRefundState());
        ctx.setOrderStatus(OrderStatus.REFUND_REQUESTED);
    }
}
