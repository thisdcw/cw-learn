package com.cw.framework.payment.v3.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 支付通知
 *
 * @author thisdcw
 * @date 2025年06月05日 15:05
 */
@Data
public class PaymentNotification {

    private String id;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("resource_type")
    private String resourceType;

    private Resource resource;

    @Data
    public static class Resource {

        private String algorithm;

        private String ciphertext;

        @JsonProperty("associated_data")
        private String associatedData;

        @JsonProperty("original_type")
        private String originalType;

        private String nonce;
    }
}
