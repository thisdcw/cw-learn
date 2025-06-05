package com.cw.framework.payment.v3.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.refund.RefundService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author thisdcw
 * @date 2025年06月05日 14:44
 */
@Data
@Configuration
public class WxPayConfig {
    private static final Logger logger = LoggerFactory.getLogger(WxPayConfig.class);

    @Resource
    private WxProperties wxProperties;

    @Bean
    public Config createConfig() {
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(wxProperties.getMchId())
                .privateKeyFromPath(wxProperties.getPrivateKeyPath())
                .merchantSerialNumber(wxProperties.getMerchantSerialNumber())
                .apiV3Key(wxProperties.getApiV3Key())
                .build();
    }

    @Bean
    public JsapiService createJsapiService(Config config) {
        return new JsapiService.Builder().config(config).build();
    }

    @Bean
    public RefundService createRefundService(Config config) {
        return new RefundService.Builder().config(config).build();
    }

}
