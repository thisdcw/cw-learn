package com.cw.design.statemachine.example;

import com.cw.design.statemachine.example.state.*;
import lombok.Data;

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

    /**
     * 在 springboot 项目中优化成自动注入
     */
    public OrderContext(OrderStatus orderStatus, boolean canRefund) {
        this.orderStatus = orderStatus;
        this.canRefund = canRefund;
        switch (orderStatus) {
            case UN_PAY:
                orderState = new UnPayState();
                break;
            case UN_SHIP:
                orderState = new UnShipState();
                break;
            case UN_RECEIVE:
                orderState = new UnConfirmState();
                break;
            case REFUND_REQUESTED:
                orderState = new UnConfirmRefundState();
                break;
            case REFUND_APPROVED:
                orderState = new ConfirmRefundState();
                break;
            case COMPLETED:
                orderState = new CompleteState();
                break;
            default:
                throw new IllegalStateException("不支持的订单类型" + orderStatus);
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
