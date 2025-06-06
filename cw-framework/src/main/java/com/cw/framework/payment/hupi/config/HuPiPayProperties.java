package com.cw.framework.payment.hupi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 虎皮椒支付配置
 *
 * @author thisdcw
 * @date 2025年06月06日 14:04
 */
@Data
@Component
@ConfigurationProperties(prefix = "hupi")
public class HuPiPayProperties {

    private String appid;

    private String appSecret;

    private String notifyUrl;

}
