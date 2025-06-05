package com.cw.framework.payment.v3.model;

import lombok.Data;

/**
 * 小程序支付响应参数
 *
 * @author thisdcw
 * @date 2025年06月05日 15:02
 */
@Data
public class MiniProgramPayResponse {
    private String timeStamp;
    private String nonceStr;
    private String packageValue;
    private String signType;
    private String paySign;
}
