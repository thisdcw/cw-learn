package com.cw.design.statemachine.example.state;

import com.cw.design.statemachine.example.OrderContext;
import com.cw.design.statemachine.example.OrderState;
import com.cw.design.statemachine.example.OrderStatus;

/**
 * 发货后允许的操作
 * @author thisdcw
 * @date 2025年12月31日 13:42
 */
public class UnConfirmState implements OrderState {

    @Override
    public void confirmReceipt(OrderContext ctx) {
        System.out.println("确认收货!");
        ctx.setOrderStatus(OrderStatus.COMPLETED);
        ctx.setOrderState(new CompleteState());
    }

    @Override
    public void cancel(OrderContext ctx) {
        System.out.println("未确认收货 - 取消订单!");
        ctx.setOrderStatus(OrderStatus.CANCELLED);
    }

}
