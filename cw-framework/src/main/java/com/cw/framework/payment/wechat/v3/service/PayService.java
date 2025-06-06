package com.cw.framework.payment.wechat.v3.service;

import com.cw.framework.payment.wechat.v3.model.MiniProgramPayRequest;
import com.cw.framework.payment.wechat.v3.model.MiniProgramPayResponse;
import com.cw.framework.payment.wechat.v3.model.PaymentNotification;
import com.cw.framework.payment.wechat.v3.model.RefundRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;

/**
 * @author thisdcw
 * @date 2025年06月05日 15:01
 */
public interface PayService {

    /**
     * 创建小程序支付订单
     *
     * @param orderRequest 订单请求参数
     * @return 小程序支付参数
     */
    MiniProgramPayResponse createOrder(MiniProgramPayRequest orderRequest);


    /**
     * 查询支付订单
     *
     * @param outTradeNo 商户订单号
     * @return 订单查询结果
     */
    Transaction queryOrder(String outTradeNo);


    /**
     * 关闭订单
     *
     * @param outTradeNo 商户订单号
     */
    void closeOrder(String outTradeNo);


    /**
     * 申请退款
     *
     * @param refundRequest 退款请求参数
     * @return 退款结果
     */
    Refund createRefund(RefundRequest refundRequest);

    /**
     * 查询退款
     *
     * @param outRefundNo 商户退款单号
     * @return 退款查询结果
     */
    Refund queryRefund(String outRefundNo);

    /**
     * 构建小程序支付参数
     *
     * @param prepayId 预支付交易会话标识
     * @return 小程序支付参数
     */
    MiniProgramPayResponse buildMiniProgramPayParams(String prepayId);

    /**
     * 生成随机字符串
     */
    String generateNonceStr();

    /**
     * 验证支付回调签名
     *
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param body      回调内容
     * @param signature 签名
     * @return 验证结果
     */
    boolean verifyNotifySignature(String timestamp, String nonce, String body, String signature);


    /**
     * 解析支付回调通知
     *
     * @param notifyBody 回调内容
     * @return 解析结果
     */
    PaymentNotification parsePaymentNotification(String notifyBody);
}
