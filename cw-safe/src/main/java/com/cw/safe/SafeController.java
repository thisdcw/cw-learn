package com.cw.safe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author thisdcw
 * @date 2025年05月21日 16:11
 */
@RestController
@RequestMapping("/safe")
public class SafeController {


    private static final Logger logger = LoggerFactory.getLogger(SafeController.class);

    @GetMapping("/test")
    public R<Void> test() {
        logger.info("test开始");


        R<Void> res = new R<>();
        res.setCode(201);
        res.setMsg("success");

        logger.info("test success");

        return res;
    }
}
