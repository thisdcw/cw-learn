package com.cw.design.statemachine.example.state;

import com.cw.design.statemachine.example.OrderContext;
import com.cw.design.statemachine.example.OrderState;
import com.cw.design.statemachine.example.OrderStatus;

/**
 * 未发货允许的操作
 *
 * @author thisdcw
 * @date 2025年12月31日 13:46
 */
public class UnShipState implements OrderState {

    @Override
    public void ship(OrderContext ctx) {
        System.out.println("发货完成!");
        ctx.setOrderStatus(OrderStatus.UN_RECEIVE);
        ctx.setOrderState(new UnReceiveState());
    }

    @Override
    public void cancel(OrderContext ctx) {
        System.out.println("未发货 - 取消订单");
        ctx.setOrderStatus(OrderStatus.CANCELLED);
    }
}
