package com.cw.framework.payment.v2.model;


import lombok.Data;

/**
 * @author thisdcw
 * @date 2025年06月05日 20:40
 */
@Data
public class OrderRequest {

    private String body;

    private String outTradeNo;

    private Integer totalFee;

    private String openid;

}
