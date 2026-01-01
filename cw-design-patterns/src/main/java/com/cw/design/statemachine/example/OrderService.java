package com.cw.design.statemachine.example;

/**
 * 订单业务
 *
 * @author thisdcw
 * @date 2025年12月31日 15:14
 */
public class OrderService {

    public void approvedRefund(OrderContext ctx) {
        ctx.approvedRefund();
    }

    public void applyRefund(OrderContext ctx) {
        if (!ctx.isCanRefund()) {
            System.out.println("此订单不允许退款");
            return;
        }
        ctx.applyRefund();
    }

    public void cancelRefund(OrderContext ctx) {
        ctx.cancelRefund();
    }

    public void comment(OrderContext ctx) {
        ctx.comment();
    }

    public void share(OrderContext ctx) {
        ctx.share();
    }

    public void pay(OrderContext ctx) {
        if (ctx.getOrderStatus() != OrderStatus.UN_PAY) {
            throw new IllegalStateException("当前状态不可支付");
        }
        ctx.pay();
    }

    public void cancel(OrderContext ctx) {
        ctx.cancel();
    }

    public void ship(OrderContext ctx) {
        ctx.ship();
    }

    public void confirm(OrderContext ctx) {
        ctx.confirm();
    }

}
