package com.cw.design.ruleworkflow;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author thisdcw
 * @date 2026年01月08日 13:54
 */
@Getter
@AllArgsConstructor
public enum GoodsType {

    /**
     * 普通商品: <br/>
     * 1.不能使用优惠券 <br/>
     * 2.能使用余额 <br/>
     * 3.能送优惠券 <br/>
     * 4.能送积分
     */
    NORMAL("普通商品", 1, false, true, true, true),

    /**
     * 优惠券专区商品: <br/>
     * 1.能使用优惠券 <br/>
     * 2.不能使用余额 <br/>
     * 3.不能送优惠券<br/>
     * 4.能送积分
     */
    COUPON_ZONE("优惠券专区商品", 2, true, false, false, false),

    /**
     * 供应链商品: <br/>
     * 1.不能使用优惠券 <br/>
     * 2.能使用余额 <br/>
     * 3.能送优惠券<br/>
     * 4.能送积分
     */
    SUPPLY_CHAIN("供应链商品", 3, false, true, true, true),

    /**
     * 商务套餐商品: <br/>
     * 1.不能使用优惠券 <br/>
     * 2.能使用余额 <br/>
     * 3.不能送优惠券<br/>
     * 4.能送积分
     */
    BUSINESS_PACKAGE("商务套餐商品", 4, false, true, false, true),

    /**
     * 商务加速套餐商品: <br/>
     * 1.不能使用优惠券 <br/>
     * 2.能使用余额 <br/>
     * 3.不能送优惠券<br/>
     * 4.能送积分
     */
    BUSINESS_ACCELERATOR("商务加速套餐商品", 5, false, true, false, true);;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 类型
     */
    private final int type;

    /**
     * 是否可使用优惠券
     */
    private final boolean canUseCoupon;

    /**
     * 是否可使用余额
     */
    private final boolean canUseAccount;

    /**
     * 是否送优惠券
     */
    private final boolean canCoupon;

    /**
     * 是否送积分
     */
    private final boolean canIntegral;

}
