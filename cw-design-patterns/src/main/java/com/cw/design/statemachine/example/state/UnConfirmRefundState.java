package com.cw.design.statemachine.example.state;


import com.cw.design.statemachine.example.OrderContext;
import com.cw.design.statemachine.example.OrderState;
import com.cw.design.statemachine.example.OrderStatus;

/**
 * 申请退款后允许的操作
 *
 * @author thisdcw
 * @date 2026年01月01日 20:35
 */
public class UnConfirmRefundState implements OrderState {

    @Override
    public void cancelRefund(OrderContext ctx) {
        if (ctx.getOrderStatus() == OrderStatus.REFUND_CANCELLED) {
            throw new IllegalStateException("不能重复取消退款");
        }
        System.out.println("取消退款");
        ctx.setOrderStatus(OrderStatus.REFUND_CANCELLED);
    }

    @Override
    public void share(OrderContext ctx) {
        System.out.println("待确认退款 - 分享订单");
    }

    @Override
    public void comment(OrderContext ctx) {
        System.out.println("待确认退款 - 评论");
    }

    @Override
    public void refund(OrderContext ctx) {
        System.out.println("退款");
        ctx.setOrderStatus(OrderStatus.REFUND_APPROVED);
        ctx.setOrderState(new ConfirmRefundState());
    }
}
