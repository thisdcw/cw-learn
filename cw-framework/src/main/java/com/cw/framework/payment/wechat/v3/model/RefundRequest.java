package com.cw.framework.payment.wechat.v3.model;

import lombok.Data;

/**
 * 退款请求参数
 *
 * @author thisdcw
 * @date 2025年06月05日 15:03
 */
@Data
public class RefundRequest {
    // 商户退款单号
    private String outRefundNo;
    // 微信订单号（可选）
    private String transactionId;
    // 商户订单号（可选）
    private String outTradeNo;
    // 退款原因
    private String reason;
    // 退款金额（分）
    private Integer refund;
    private Integer total;
}
