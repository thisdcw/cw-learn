package com.cw.design.statemachine.example.state;

import com.cw.design.statemachine.example.OrderContext;
import com.cw.design.statemachine.example.OrderState;
import com.cw.design.statemachine.example.OrderStatus;

/**
 * @author thisdcw
 * @date 2025年12月31日 13:42
 */
public class UnConfirmState implements OrderState {

    @Override
    public void confirmReceipt(OrderContext ctx) {
        System.out.println("确认收货!");
        ctx.setOrderStatus(OrderStatus.COMPLETED);
    }

    @Override
    public void cancel(OrderContext ctx) {
        System.out.println("未确认收货 - 取消订单!");
        ctx.setOrderStatus(OrderStatus.CANCELLED);
    }

}
