package com.cw.design.statemachine.example;

import com.cw.design.statemachine.example.state.UnConfirmState;
import com.cw.design.statemachine.example.state.UnPayState;
import com.cw.design.statemachine.example.state.UnShipState;
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

    /**
     * 在 springboot 项目中优化成自动注入
     */
    public OrderContext(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
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
            default:
                throw new IllegalStateException("不支持的订单类型" + orderStatus);
        }
    }

    public void pay() {
        orderState.pay(this);
    }

    public void ship() {
        orderState.ship(this);
    }

    public void cancel() {
        orderState.cancel(this);
    }

    public void confirm() {
        orderState.confirmReceipt(this);
    }
}
