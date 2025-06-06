package com.cw.framework.payment.hupi.controller;

import com.cw.framework.payment.hupi.HuPiPayService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author thisdcw
 * @date 2025年06月06日 14:12
 */
@RestController
@RequestMapping("/pay")
public class HuPiPayController {

    @Resource
    private HuPiPayService payService;

    @PostMapping("/qrcode")
    public String qrcode(@RequestParam String orderNo, @RequestParam int amount, @RequestParam String body) {
        return payService.createQrCode(orderNo, amount, body);
    }

    @GetMapping("/query")
    public String query(@RequestParam String orderNo) {
        return payService.queryOrder(orderNo);
    }

    @PostMapping("/close")
    public String close(@RequestParam String orderNo) {
        return payService.closeOrder(orderNo);
    }

}
