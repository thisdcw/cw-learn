package com.cw.design.statemachine.example;

import com.cw.design.statemachine.example.state.*;
import lombok.Data;

import java.util.Map;
import java.util.Set;

import static com.cw.design.statemachine.example.OrderAction.*;
import static com.cw.design.statemachine.example.OrderAction.DO_REFUND_APPROVED;
import static com.cw.design.statemachine.example.OrderStatus.*;

/**
 * 订单上下文
 *
 * @author thisdcw
 * @date 2025年12月31日 9:36
 */
@Data
public class OrderContext {

    private OrderStatus orderStatus;

    private OrderState orderState;

    //业务层面需要指定某些商品是否允许退款
    private boolean canRefund;

    Map<OrderStatus, Set<OrderAction>> ALLOW_MAP = Map.of(
            UN_PAY, Set.of(DO_PAY, DO_CANCEL),
            UN_SHIP, Set.of(DO_SHIP, DO_REFUND_REQUEST),
            UN_RECEIVE, Set.of(DO_RECEIVE, DO_REFUND_REQUEST),
            REFUND_REQUESTED, Set.of(DO_REFUND_APPROVED, DO_REFUND_CANCEL),
            COMPLETED, Set.of()
    );


    /**
     * 在 springboot 项目中优化成自动注入
     */
    public OrderContext(OrderStatus orderStatus, boolean canRefund) {
        this.orderStatus = orderStatus;
        this.canRefund = canRefund;
        this.orderState = orderStatus.getOrderState();
        if (orderState == null) {
            throw new IllegalStateException("改状态不允许操作");
        }
    }

    public void ruleCheck(OrderContext ctx, OrderAction action) {
        if (!ALLOW_MAP
                .getOrDefault(ctx.getOrderStatus(), Set.of())
                .contains(action)) {
            throw new IllegalStateException("状态不允许该操作");
        }
    }


    /**
     * 评论
     */
    public void comment() {
        orderState.comment(this);
    }

    /**
     * 分享
     */
    public void share() {
        orderState.share(this);
    }

    /**
     * 取消退款
     */
    public void cancelRefund() {
        orderState.cancelRefund(this);
    }

    /**
     * 同意退款
     */
    public void approvedRefund() {
        orderState.refund(this);
    }

    /**
     * 申请退款
     */
    public void applyRefund() {
        orderState.applyRefund(this);
    }

    /**
     * 支付订单
     */
    public void pay() {
        orderState.pay(this);
    }

    /**
     * 发货
     */
    public void ship() {
        orderState.ship(this);
    }

    /**
     * 取消订单
     */
    public void cancel() {
        orderState.cancel(this);
    }

    /**
     * 确认收货
     */
    public void confirm() {
        orderState.confirmReceipt(this);
    }
}
