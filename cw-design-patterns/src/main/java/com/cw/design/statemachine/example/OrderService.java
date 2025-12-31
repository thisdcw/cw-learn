package com.cw.design.statemachine.example;

/**
 * @author thisdcw
 * @date 2025年12月31日 15:14
 */
public class OrderService {

    public void pay(OrderContext ctx) {
        if (ctx.getOrderStatus() != OrderStatus.UN_PAY) {
            throw new IllegalStateException("当前状态不可支付");
        }
        ctx.pay();
    }

    public void cancel(OrderContext ctx) {
        if (ctx.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("订单已取消");
        }
        ctx.cancel();
    }

    public void ship(OrderContext ctx) {
        if (ctx.getOrderStatus() != OrderStatus.UN_SHIP) {
            throw new IllegalStateException("当前状态不可发货");
        }
        ctx.ship();
    }

    public void confirm(OrderContext ctx) {
        if (ctx.getOrderStatus() != OrderStatus.UN_RECEIVE) {
            throw new IllegalStateException("当前状态不可确认收货");
        }
        ctx.confirm();
    }

}
