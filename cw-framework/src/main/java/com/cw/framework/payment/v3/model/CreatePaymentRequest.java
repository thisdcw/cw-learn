package com.cw.framework.payment.v3.model;

import com.wechat.pay.java.service.payments.jsapi.model.SceneInfo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建支付请求参数
 *
 * @author thisdcw
 * @date 2025年06月05日 15:33
 */
@Data
public class CreatePaymentRequest {

    @NotBlank(message = "商品描述不能为空")
    private String description;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额不能小于0.01元")
    private Integer amount;

    @NotBlank(message = "用户openid不能为空")
    private String openid;

    /**
     * 附加数据，可选
     */
    private String attach;

    /**
     * 商品标记，可选
     */
    private String goodsTag;

    /**
     * 订单优惠标记，可选
     */
    private String detail;

    /**
     * 场景信息，可选
     */
    private SceneInfo sceneInfo;

}
