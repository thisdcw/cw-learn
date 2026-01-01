package com.cw.design.statemachine.example;

/**
 * 订单业务基础能力
 *
 * @author thisdcw
 * @date 2025年12月31日 9:36
 */
public interface OrderState {

    /**
     * 支付
     */
    default void pay(OrderContext ctx) {
        throw new IllegalStateException("不可支付");
    }


    /**
     * 取消订单
     */
    default void cancel(OrderContext ctx) {
        throw new IllegalStateException("不可取消");
    }

    /**
     * 发货
     */
    default void ship(OrderContext ctx) {
        throw new IllegalStateException("不可发货");
    }

    /**
     * 确认收货
     */
    default void confirmReceipt(OrderContext ctx) {
        throw new IllegalStateException("不可确认收货");
    }

    /**
     * 分享
     */
    default void share(OrderContext ctx){
        throw new IllegalStateException("不允许分享");
    }

    /**
     * 评论
     */
    default void comment(OrderContext ctx){
        throw new IllegalStateException("不允许评论");
    }

    /**
     * 申请退款
     */
    default void applyRefund(OrderContext ctx){
        throw new IllegalStateException("不允许申请退款");
    }

    /**
     * 退款
     */
    default void refund(OrderContext ctx){
        throw new IllegalStateException("不允许退款");
    }

    /**
     * 取消退款
     */
    default void cancelRefund(OrderContext ctx){
        throw new IllegalStateException("不允许取消退款");
    }
}
