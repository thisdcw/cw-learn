package com.cw.design.statemachine.example.state;

import com.cw.design.statemachine.example.OrderContext;
import com.cw.design.statemachine.example.OrderState;
import com.cw.design.statemachine.example.OrderStatus;

/**
 * 未支付的操作
 *
 * @author thisdcw
 * @date 2025年12月31日 13:38
 */
public class UnPayState implements OrderState {

    @Override
    public void pay(OrderContext ctx) {
        System.out.println("支付成功!");
        ctx.setOrderStatus(OrderStatus.UN_SHIP);
        ctx.setOrderState(new UnShipState());
    }

    @Override
    public void cancel(OrderContext ctx) {
        System.out.println("待支付 - 取消订单");
        ctx.setOrderStatus(OrderStatus.CANCELLED);
    }
}
