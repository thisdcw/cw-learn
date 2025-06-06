package com.cw.framework.payment.hupi.config;

import com.cw.framework.payment.hupi.HuPiPayService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 虎皮椒支付注入
 * @author thisdcw
 * @date 2025年06月06日 14:10
 */
@Configuration
public class HuPiPayConfig {

    @Resource
    private HuPiPayProperties huPiPayProperties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HuPiPayService getHuPiPayService(RestTemplate restTemplate) {
        return new HuPiPayService(huPiPayProperties, restTemplate);
    }

}
