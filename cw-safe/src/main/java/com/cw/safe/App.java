package com.cw.safe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 应用程序入口类
 *
 * @author thisdcw
 */
@SpringBootApplication
public class App {

    public static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(App.class, args);
            log.info("应用启动成功!");
        } catch (Exception e) {
            log.error("应用启动失败!", e);
            System.exit(1);
        }
    }

}
