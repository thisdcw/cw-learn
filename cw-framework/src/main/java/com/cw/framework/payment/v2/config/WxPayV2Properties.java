package com.cw.framework.payment.v2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author thisdcw
 * @date 2025年06月05日 16:32
 */
@Data
@ConfigurationProperties(prefix = "wechat.pay.v2")
public class WxPayV2Properties {
    /**
     * 设置微信公众号或者小程序等的appid
     */
    private String appId;

    /**
     * 微信支付商户号
     * (V3商户模式需要)
     */
    private String mchId;

    /**
     * 微信支付商户密钥
     */
    private String mchKey;


    /**
     * 支付回调通知
     */
    private String notifyUrl;

}
