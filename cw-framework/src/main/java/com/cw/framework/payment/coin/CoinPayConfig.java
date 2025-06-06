package com.cw.framework.payment.coin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author thisdcw
 * @date 2025年06月06日 14:31
 */
@Configuration
public class CoinPayConfig {

    @Resource
    private CoinProperties coinProperties;

    @Bean
    public CoinPayService getCoinPayService() {
        return new CoinPayService(coinProperties);
    }
}
