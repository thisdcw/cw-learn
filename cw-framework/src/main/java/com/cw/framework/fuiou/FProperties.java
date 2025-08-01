package com.cw.framework.fuiou;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author thisdcw
 * @date 2025年07月28日 11:28
 */
@Component
@ConfigurationProperties(prefix = "fy")
@Data
public class FProperties {


    private String merchantPrivateKey;

}
