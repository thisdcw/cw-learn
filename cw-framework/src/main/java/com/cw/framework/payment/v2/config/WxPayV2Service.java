package com.cw.framework.payment.v2.config;

import cn.hutool.http.HttpUtil;
import com.cw.framework.payment.v2.utils.WxPayV2Util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author thisdcw
 * @date 2025年06月05日 16:38
 */
public class WxPayV2Service {

    private final WxPayV2Properties config;

    public WxPayV2Service(WxPayV2Properties config) {
        this.config = config;
    }

    public String unifiedOrder(String body, String outTradeNo, Integer totalFee, String openid, String clientIp) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("appid", config.getAppId());
        data.put("mch_id", config.getMchId());
        data.put("nonce_str", WxPayV2Util.generateNonceStr());
        data.put("body", body);
        data.put("out_trade_no", outTradeNo);
        data.put("total_fee", String.valueOf(totalFee));
        data.put("spbill_create_ip", clientIp);
        data.put("notify_url", config.getNotifyUrl());
        data.put("trade_type", "JSAPI");
        data.put("openid", openid);

        String sign = WxPayV2Util.generateSignature(data, config.getMchKey());
        data.put("sign", sign);

        String xml = WxPayV2Util.mapToXml(data);

        // POST 到统一下单接口
        return HttpUtil.post("https://api.mch.weixin.qq.com/pay/unifiedorder", xml);

    }

    public String queryOrder(String outTradeNo) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("appid", config.getAppId());
        params.put("mch_id", config.getMchId());
        params.put("out_trade_no", outTradeNo);
        params.put("nonce_str", WxPayV2Util.generateNonceStr());

        params.put("sign", WxPayV2Util.generateSignature(params, config.getMchKey()));
        String requestXml = WxPayV2Util.mapToXml(params);

        return HttpUtil.post("https://api.mch.weixin.qq.com/pay/orderquery", requestXml);
    }


}

