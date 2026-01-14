package com.cw.design.statemachine.example.state;


import com.cw.design.statemachine.example.OrderContext;
import com.cw.design.statemachine.example.OrderState;
import com.cw.design.statemachine.example.OrderStatus;

/**
 * 退款完成的操作
 * @author thisdcw
 * @date 2026年01月01日 20:35
 */
public class ConfirmRefundState implements OrderState {

    @Override
    public void share(OrderContext ctx) {
        System.out.println("退款 - 分享订单");
    }

    @Override
    public void comment(OrderContext ctx) {
        System.out.println("退款 - 评论");
    }
}
