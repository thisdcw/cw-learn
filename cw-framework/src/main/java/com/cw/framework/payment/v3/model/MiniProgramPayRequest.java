package com.cw.framework.payment.v3.model;

import lombok.Data;

/**
 * 小程序支付请求参数
 *
 * @author thisdcw
 * @date 2025年06月05日 15:04
 */
@Data
public class MiniProgramPayRequest {
    // 商品描述
    private String description;
    // 商户订单号
    private String outTradeNo;
    // 订单金额（分）
    private Integer total;
    // 用户openid
    private String openid;
    private String timeExpire;
}
