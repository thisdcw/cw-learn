package com.cw.framework.payment.v3.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author thisdcw
 * @date 2025年06月05日 15:25
 */
@Component
@Data
@ConfigurationProperties(prefix = "wechat.pay.v3")
public class WxProperties {

    private String appId;

    private String mchId;

    private String privateKeyPath;

    private String merchantSerialNumber;

    private String apiV3Key;

    private String notifyUrl;

}
