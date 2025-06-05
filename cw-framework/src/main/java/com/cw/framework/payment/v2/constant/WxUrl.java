package com.cw.framework.payment.v2.constant;


/**
 * @author thisdcw
 * @date 2025年06月05日 20:28
 */
public interface WxUrl {

    String CREATE_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    String QUERY_ORDER = "https://api.mch.weixin.qq.com/pay/orderquery";

    String CLOSE_ORDER = "https://api.mch.weixin.qq.com/pay/closeorder";
}
