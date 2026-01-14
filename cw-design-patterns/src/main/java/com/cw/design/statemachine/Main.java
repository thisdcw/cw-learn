package com.cw.design.statemachine;

import com.cw.design.statemachine.example.OrderContext;
import com.cw.design.statemachine.example.OrderService;
import com.cw.design.statemachine.example.OrderStatus;

/**
 * @author thisdcw
 * @date 2025年12月31日 9:38
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("测试可退款订单");
        OrderContext context = new OrderContext(OrderStatus.UN_PAY, true);
        OrderService orderService = new OrderService();

        orderService.pay(context);
        orderService.cancel(context);
        orderService.ship(context);
        orderService.confirm(context);
    }
}
