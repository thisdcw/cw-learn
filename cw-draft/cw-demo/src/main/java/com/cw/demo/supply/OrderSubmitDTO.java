package com.cw.demo.supply;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author thisdcw
 * @date 2025年04月22日 9:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderSubmitDTO extends BaseReq{
    /**
     * 订单备注
     */
    private String remark;
    /**
     * 收货地区行政编码，例如:省编码,市编码,区编码 以英文‘,’逗号隔开
     */
    private String shipAreaCode;
    /**
     * 商品信息（数组）sku和购买数量
     */
    private List<SkuDTO> sku_list;
    /**
     * 第三方订单号
     */
    private String third_sn;

    /**
     * 收件人详细地址(注：街道地址需要拼接到详细地址里)
     */
    private String user_address;
    /**
     * 身份证号码，跨境商品必填
     */
//    private String user_idcard;
    /**
     * 收件人电话
     */
    private String user_mobile;
    /**
     * 收件人姓名
     */
    private String user_name;
    /**
     * 真实姓名，跨境商品必填
     */
//    private String user_realname;


    @Data
    public static class SkuList {

        private Integer sku_id;

        private Integer sku_num;
    }
}


