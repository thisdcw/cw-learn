package com.cw.framework.payment.coin;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * @author thisdcw
 * @date 2025年06月06日 14:27
 */
public class CoinPayService {

    private final CoinProperties config;

    public CoinPayService(CoinProperties config) {
        this.config = config;
    }

    public String createTransaction(String amount, String currency, String buyerEmail, String itemName) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("version", "1");
        params.add("cmd", "create_transaction");
        params.add("key", config.getPublicKey());
        params.add("amount", amount);
        params.add("currency1", currency);
        params.add("currency2", currency);
        params.add("buyer_email", buyerEmail);
        params.add("item_name", itemName);
        params.add("ipn_url", config.getCallbackUrl());

        String body = params.toSingleValueMap().entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("HMAC", CoinPaymentsUtil.hmacSha512(body, config.getPrivateKey()));

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("https://www.coinpayments.net/api.php", request, String.class);
        return response.getBody();
    }
}
