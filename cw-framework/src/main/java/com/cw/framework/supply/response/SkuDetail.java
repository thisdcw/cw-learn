package com.cw.framework.supply.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author thisdcw
 * @date 2025年06月11日 13:38
 */
@Data
public class SkuDetail {

    @SerializedName("sku_id")
    private Long skuId;

    private String suk;
    private List<Attr> attr;
    private Long stock;
    private Long sales;
    private String price;
    private String image;

    @SerializedName("bar_code")
    private String barCode;

    @SerializedName("market_price")
    private String marketPrice;

    @SerializedName("group_price")
    private String groupPrice;

    private Double weight;
    private String volume;

    @SerializedName("is_show")
    private Integer isShow;

    @SerializedName("buy_start_qty")
    private Long buyStartQty;

    @SerializedName("is_overseas")
    private Long isOverseas;

    @SerializedName("service_price")
    private Double servicePrice;

    @SerializedName("skuDesc")
    private SkuDesc skuDesc;

    @Data
    public static class Attr {
        private String name;
        private String value;
    }

    @Data
    public static class SkuDesc {
        private String skuname;
        private String description;
    }
}
