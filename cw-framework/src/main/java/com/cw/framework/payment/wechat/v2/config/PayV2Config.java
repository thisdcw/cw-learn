package com.cw.framework.payment.wechat.v2.config;

import com.cw.framework.payment.wechat.v2.service.WxPayV2Service;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author thisdcw
 * @date 2025年06月05日 16:33
 */
@Component
@EnableConfigurationProperties(WxPayV2Properties.class)
public class PayV2Config {

    @Resource
    private WxPayV2Properties properties;

    @Bean
    public WxPayV2Service wxPayV2Client() {
        return new WxPayV2Service(properties);
    }
}
