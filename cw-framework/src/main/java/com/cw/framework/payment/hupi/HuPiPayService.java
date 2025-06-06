package com.cw.framework.payment.hupi;

import com.cw.framework.payment.hupi.config.HuPiPayProperties;
import com.cw.framework.payment.hupi.utils.SignUtil;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author thisdcw
 * @date 2025年06月06日 14:07
 */
public class HuPiPayService {

    private final HuPiPayProperties config;

    private final RestTemplate restTemplate;


    public HuPiPayService(HuPiPayProperties config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    private String api(String url, Map<String, String> data) {
        data.put("appid", config.getAppid());
        data.put("sign", SignUtil.md5(SignUtil.createSign(data, config.getAppSecret())));
        return restTemplate.postForObject(url, data, String.class);
    }

    public String createQrCode(String outTradeNo, int totalFee, String body) {
        Map<String, String> data = new HashMap<>();
        data.put("out_trade_no", outTradeNo);
        data.put("total_fee", String.valueOf(totalFee));
        data.put("body", body);
        data.put("notify_url", config.getNotifyUrl());
        return api("https://api.hupi.io/api/native", data);
    }

    public String queryOrder(String outTradeNo) {
        Map<String, String> data = new HashMap<>();
        data.put("out_trade_no", outTradeNo);
        return api("https://api.hupi.io/api/orderquery", data);
    }

    public String closeOrder(String outTradeNo) {
        Map<String, String> data = new HashMap<>();
        data.put("out_trade_no", outTradeNo);
        return api("https://api.hupi.io/api/close", data);
    }
}

