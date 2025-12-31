package com.cw.design.statemachine.example.state;

import com.cw.design.statemachine.example.OrderContext;
import com.cw.design.statemachine.example.OrderState;
import com.cw.design.statemachine.example.OrderStatus;

/**
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
}
