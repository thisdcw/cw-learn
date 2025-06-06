package com.cw.framework.payment.coin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author thisdcw
 * @date 2025年06月06日 14:25
 */
@Data
@Component
@ConfigurationProperties(prefix = "coin")
public class CoinProperties {

    private String publicKey;
    private String privateKey;
    private String ipnSecret;
    private String callbackUrl;

}
