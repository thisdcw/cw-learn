package com.cw.design.statemachine.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单状态机测试类
 * 测试所有状态下的用户操作场景
 *
 * @author thisdcw
 * @date 2026年01月01日
 */
@DisplayName("订单状态机测试")
class OrderStateMachineTest {

    @Test
    @DisplayName("测试正常订单流程: 下单->支付->发货->确认收货->完成")
    void testNormalOrderFlow() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_PAY, true);

        // 初始状态: 待支付
        assertEquals(OrderStatus.UN_PAY, context.getOrderStatus());

        // 支付
        service.pay(context);
        assertEquals(OrderStatus.UN_SHIP, context.getOrderStatus());

        // 发货
        service.ship(context);
        assertEquals(OrderStatus.UN_RECEIVE, context.getOrderStatus());

        // 确认收货
        service.confirm(context);
        assertEquals(OrderStatus.COMPLETED, context.getOrderStatus());
    }

    @Test
    @DisplayName("测试待支付状态取消订单")
    void testCancelOrderAtUnPayState() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_PAY, true);

        // 待支付状态可以取消订单
        service.cancel(context);
        assertEquals(OrderStatus.CANCELLED, context.getOrderStatus());
    }

    @Test
    @DisplayName("测试待发货状态取消订单")
    void testCancelOrderAtUnShipState() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_PAY, true);

        // 先支付
        service.pay(context);
        assertEquals(OrderStatus.UN_SHIP, context.getOrderStatus());

        // 待发货状态可以取消订单
        service.cancel(context);
        assertEquals(OrderStatus.CANCELLED, context.getOrderStatus());
    }

    @Test
    @DisplayName("测试待收货状态取消订单")
    void testCancelOrderAtUnReceiveState() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_PAY, true);

        // 支付 -> 发货
        service.pay(context);
        service.ship(context);
        assertEquals(OrderStatus.UN_RECEIVE, context.getOrderStatus());

        // 待收货状态可以取消订单
        service.cancel(context);
        assertEquals(OrderStatus.CANCELLED, context.getOrderStatus());
    }

    @Test
    @DisplayName("测试非待支付状态不能支付")
    void testPayOnlyAtUnPayState() {
        OrderService service = new OrderService();

        // 待发货状态不能支付
        OrderContext unShipContext = new OrderContext(OrderStatus.UN_SHIP, true);
        assertThrows(IllegalStateException.class, () -> service.pay(unShipContext));

        // 待收货状态不能支付
        OrderContext unReceiveContext = new OrderContext(OrderStatus.UN_RECEIVE, true);
        assertThrows(IllegalStateException.class, () -> service.pay(unReceiveContext));

        // 已完成状态不能支付
        OrderContext completedContext = new OrderContext(OrderStatus.COMPLETED, true);
        assertThrows(IllegalStateException.class, () -> service.pay(completedContext));
    }

    @Test
    @DisplayName("测试非待发货状态不能发货")
    void testShipOnlyAtUnShipState() {
        OrderService service = new OrderService();

        // 待支付状态不能发货
        OrderContext unPayContext = new OrderContext(OrderStatus.UN_PAY, true);
        assertThrows(IllegalStateException.class, () -> service.ship(unPayContext));

        // 待收货状态不能发货
        OrderContext unReceiveContext = new OrderContext(OrderStatus.UN_RECEIVE, true);
        assertThrows(IllegalStateException.class, () -> service.ship(unReceiveContext));

        // 已完成状态不能发货
        OrderContext completedContext = new OrderContext(OrderStatus.COMPLETED, true);
        assertThrows(IllegalStateException.class, () -> service.ship(completedContext));
    }

    @Test
    @DisplayName("测试非待收货状态不能确认收货")
    void testConfirmOnlyAtUnReceiveState() {
        OrderService service = new OrderService();

        // 待支付状态不能确认收货
        OrderContext unPayContext = new OrderContext(OrderStatus.UN_PAY, true);
        assertThrows(IllegalStateException.class, () -> service.confirm(unPayContext));

        // 待发货状态不能确认收货
        OrderContext unShipContext = new OrderContext(OrderStatus.UN_SHIP, true);
        assertThrows(IllegalStateException.class, () -> service.confirm(unShipContext));

        // 已完成状态不能确认收货
        OrderContext completedContext = new OrderContext(OrderStatus.COMPLETED, true);
        assertThrows(IllegalStateException.class, () -> service.confirm(completedContext));
    }

    @Test
    @DisplayName("测试完成后可以评论")
    void testCommentAfterComplete() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.COMPLETED, true);

        // 已完成状态可以评论
        assertDoesNotThrow(() -> service.comment(context));
    }

    @Test
    @DisplayName("测试完成后可以分享")
    void testShareAfterComplete() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.COMPLETED, true);

        // 已完成状态可以分享
        assertDoesNotThrow(() -> service.share(context));
    }

    @Test
    @DisplayName("测试申请退款流程: 完成->申请退款->同意退款")
    void testRefundFlow() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.COMPLETED, true);

        assertEquals(OrderStatus.COMPLETED, context.getOrderStatus());

        // 申请退款
        service.applyRefund(context);
        assertEquals(OrderStatus.REFUND_REQUESTED, context.getOrderStatus());

        // 同意退款
        service.approvedRefund(context);
        assertEquals(OrderStatus.REFUND_APPROVED, context.getOrderStatus());
    }

    @Test
    @DisplayName("测试申请退款后取消退款")
    void testCancelRefundFlow() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.COMPLETED, true);

        // 申请退款
        service.applyRefund(context);
        assertEquals(OrderStatus.REFUND_REQUESTED, context.getOrderStatus());

        // 取消退款
        service.cancelRefund(context);
        assertEquals(OrderStatus.REFUND_CANCELLED, context.getOrderStatus());
    }

    @Test
    @DisplayName("测试非完成状态不能申请退款")
    void testApplyRefundOnlyAtComplete() {
        OrderService service = new OrderService();

        // 待支付状态不能申请退款
        OrderContext unPayContext = new OrderContext(OrderStatus.UN_PAY, true);
        assertThrows(IllegalStateException.class, () -> service.applyRefund(unPayContext));

        // 待发货状态不能申请退款
        OrderContext unShipContext = new OrderContext(OrderStatus.UN_SHIP, true);
        assertThrows(IllegalStateException.class, () -> service.applyRefund(unShipContext));

        // 待收货状态不能申请退款
        OrderContext unReceiveContext = new OrderContext(OrderStatus.UN_RECEIVE, true);
        assertThrows(IllegalStateException.class, () -> service.applyRefund(unReceiveContext));
    }

    @Test
    @DisplayName("测试不允许退款的商品")
    void testNonRefundableProduct() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.COMPLETED, false);

        // 不允许退款的商品不能申请退款
        service.applyRefund(context);
        // 状态不会改变,因为在OrderService层被拦截
        assertEquals(OrderStatus.COMPLETED, context.getOrderStatus());
    }

    @Test
    @DisplayName("测试申请退款状态可以评论")
    void testCommentAtRefundRequested() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.REFUND_REQUESTED, true);

        // 申请退款状态可以评论
        assertDoesNotThrow(() -> service.comment(context));
    }

    @Test
    @DisplayName("测试申请退款状态可以分享")
    void testShareAtRefundRequested() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.REFUND_REQUESTED, true);

        // 申请退款状态可以分享
        assertDoesNotThrow(() -> service.share(context));
    }

    @Test
    @DisplayName("测试同意退款状态可以评论")
    void testCommentAtRefundApproved() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.REFUND_APPROVED, true);

        // 同意退款状态可以评论
        assertDoesNotThrow(() -> service.comment(context));
    }

    @Test
    @DisplayName("测试同意退款状态可以分享")
    void testShareAtRefundApproved() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.REFUND_APPROVED, true);

        // 同意退款状态可以分享
        assertDoesNotThrow(() -> service.share(context));
    }

    @Test
    @DisplayName("测试非申请退款状态不能取消退款")
    void testCancelRefundOnlyAtRefundRequested() {
        OrderService service = new OrderService();

        // 已完成状态不能取消退款
        OrderContext completedContext = new OrderContext(OrderStatus.COMPLETED, true);
        assertThrows(IllegalStateException.class, () -> service.cancelRefund(completedContext));

        // 同意退款状态不能取消退款
        OrderContext approvedContext = new OrderContext(OrderStatus.REFUND_APPROVED, true);
        assertThrows(IllegalStateException.class, () -> service.cancelRefund(approvedContext));
    }

    @Test
    @DisplayName("测试非申请退款状态不能同意退款")
    void testApprovedRefundOnlyAtRefundRequested() {
        OrderService service = new OrderService();

        // 已完成状态不能同意退款
        OrderContext completedContext = new OrderContext(OrderStatus.COMPLETED, true);
        assertThrows(IllegalStateException.class, () -> service.approvedRefund(completedContext));

        // 同意退款状态不能重复同意
        OrderContext approvedContext = new OrderContext(OrderStatus.REFUND_APPROVED, true);
        assertThrows(IllegalStateException.class, () -> service.approvedRefund(approvedContext));
    }

    @Test
    @DisplayName("测试待支付状态不能评论")
    void testCannotCommentAtUnPay() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_PAY, true);

        assertThrows(IllegalStateException.class, () -> service.comment(context));
    }

    @Test
    @DisplayName("测试待支付状态不能分享")
    void testCannotShareAtUnPay() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_PAY, true);

        assertThrows(IllegalStateException.class, () -> service.share(context));
    }

    @Test
    @DisplayName("测试待发货状态不能评论")
    void testCannotCommentAtUnShip() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_SHIP, true);

        assertThrows(IllegalStateException.class, () -> service.comment(context));
    }

    @Test
    @DisplayName("测试待发货状态不能分享")
    void testCannotShareAtUnShip() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_SHIP, true);

        assertThrows(IllegalStateException.class, () -> service.share(context));
    }

    @Test
    @DisplayName("测试待收货状态不能评论")
    void testCannotCommentAtUnReceive() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_RECEIVE, true);

        assertThrows(IllegalStateException.class, () -> service.comment(context));
    }

    @Test
    @DisplayName("测试待收货状态不能分享")
    void testCannotShareAtUnReceive() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_RECEIVE, true);

        assertThrows(IllegalStateException.class, () -> service.share(context));
    }

    @Test
    @DisplayName("测试完整退款流程")
    void testCompleteRefundProcess() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.UN_PAY, true);

        // 正常流程到完成
        service.pay(context);
        service.ship(context);
        service.confirm(context);
        assertEquals(OrderStatus.COMPLETED, context.getOrderStatus());

        // 申请退款
        service.applyRefund(context);
        assertEquals(OrderStatus.REFUND_REQUESTED, context.getOrderStatus());

        // 可以评论和分享
        assertDoesNotThrow(() -> service.comment(context));
        assertDoesNotThrow(() -> service.share(context));

        // 同意退款
        service.approvedRefund(context);
        assertEquals(OrderStatus.REFUND_APPROVED, context.getOrderStatus());

        // 退款后仍可评论和分享
        assertDoesNotThrow(() -> service.comment(context));
        assertDoesNotThrow(() -> service.share(context));
    }

    @Test
    @DisplayName("测试取消退款后不能再次取消")
    void testCannotCancelAfterRefundCancelled() {
        OrderService service = new OrderService();
        OrderContext context = new OrderContext(OrderStatus.REFUND_REQUESTED, true);

        // 取消退款
        service.cancelRefund(context);
        assertEquals(OrderStatus.REFUND_CANCELLED, context.getOrderStatus());

        // 退款取消后不能再次取消
        assertThrows(IllegalStateException.class, () -> service.cancelRefund(context));
    }
}
