package com.cw.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 应用程序入口类
 */
@SpringBootApplication
public class CwFrameworkApplication {

    public static final Logger log = LoggerFactory.getLogger(CwFrameworkApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(CwFrameworkApplication.class, args);
            log.info("应用启动成功!");
        } catch (Exception e) {
            log.error("应用启动失败!", e);
            System.exit(1);
        }
    }

    /**
     * 配置跨域访问
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .maxAge(3600);
            }
        };
    }
}
