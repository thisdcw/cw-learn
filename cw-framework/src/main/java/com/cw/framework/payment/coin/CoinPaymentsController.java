package com.cw.framework.payment.coin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author thisdcw
 * @date 2025年06月06日 14:29
 */
public class CoinPaymentsController {

    @Resource
    private CoinPayService coinService;

    @Resource
    private CoinProperties config;

    @PostMapping("/create")
    public String create(@RequestParam String amount, @RequestParam String currency, @RequestParam String email, @RequestParam String name) {
        return coinService.createTransaction(amount, currency, email, name);
    }

    @PostMapping("/ipn")
    public ResponseEntity<String> ipn(HttpServletRequest request) throws IOException {
        String hmac = request.getHeader("HMAC");
        String body = new BufferedReader(new InputStreamReader(request.getInputStream()))
                .lines().collect(Collectors.joining("\n"));

        String expectedHmac = CoinPaymentsUtil.hmacSha512(body, config.getIpnSecret());
        if (!expectedHmac.equalsIgnoreCase(hmac)) {
            return ResponseEntity.status(403).body("Invalid HMAC");
        }

        // TODO: parse POST body and handle order status
        return ResponseEntity.ok("IPN Received");
    }

}
