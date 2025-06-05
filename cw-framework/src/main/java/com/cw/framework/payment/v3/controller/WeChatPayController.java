package com.cw.framework.payment.v3.controller;

import com.cw.framework.payment.v3.service.PayService;
import com.cw.framework.payment.v3.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author thisdcw
 * @date 2025年06月05日 14:59
 */
@RestController
@RequestMapping("/api/wechat/pay")
public class WeChatPayController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatPayController.class);

    @Resource
    private PayService payService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest request) {
        try {
            // 构建订单请求
            MiniProgramPayRequest payRequest = new MiniProgramPayRequest();
            payRequest.setDescription(request.getDescription());
            payRequest.setOutTradeNo(generateOrderNo());
            payRequest.setTotal(request.getAmount()); // 金额单位：分
            payRequest.setOpenid(request.getOpenid());

            // 创建支付订单
            MiniProgramPayResponse payResponse = payService.createOrder(payRequest);

            return ResponseEntity.ok(payResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 查询订单状态
     */
    @GetMapping("/query/{outTradeNo}")
    public ResponseEntity<?> queryOrder(@PathVariable String outTradeNo) {
        try {
            Transaction transaction = payService.queryOrder(outTradeNo);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("查询订单失败: " + e.getMessage());
        }
    }

    /**
     * 申请退款
     */
    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestBody RefundRequest request) {
        try {
            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setOutRefundNo(generateRefundNo());
            refundRequest.setOutTradeNo(request.getOutTradeNo());
            refundRequest.setReason(request.getReason());
            refundRequest.setRefund(request.getRefund());
            refundRequest.setTotal(request.getTotal());

            Refund refund = payService.createRefund(refundRequest);
            return ResponseEntity.ok(refund);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("申请退款失败: " + e.getMessage());
        }
    }

    /**
     * 支付结果通知处理
     */
    @PostMapping("/notify")
    public ResponseEntity<String> paymentNotify(
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestBody String body) {

        try {
            // 验证签名
            boolean isValid = payService.verifyNotifySignature(timestamp, nonce, body, signature);
            if (!isValid) {
                return ResponseEntity.badRequest().body("签名验证失败");
            }

            // 解析通知内容
            PaymentNotification notification =
                    payService.parsePaymentNotification(body);

            // 处理支付结果
            if ("TRANSACTION.SUCCESS".equals(notification.getEventType())) {
                // 支付成功处理逻辑
                handlePaymentSuccess(notification);
            }

            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            logger.error("处理支付通知失败", e);
            return ResponseEntity.status(500).body("处理失败");
        }
    }

    private void handlePaymentSuccess(PaymentNotification notification) {
        // 实现您的支付成功处理逻辑
        // 例如：更新订单状态、发送通知等
    }

    private String generateOrderNo() {
        return "ORDER_" + System.currentTimeMillis();
    }

    private String generateRefundNo() {
        return "REFUND_" + System.currentTimeMillis();
    }
}
