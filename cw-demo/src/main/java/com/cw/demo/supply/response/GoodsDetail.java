package com.cw.demo.supply.response;

import lombok.Data;

import java.util.List;

/**
 * @author thisdcw
 * @date 2025年06月11日 11:00
 */
@Data
public class GoodsDetail {
    private Long spu_id;
    private Long channel_id;
    private Long brand_id;
    private String class_id;
    private String image;
    private List<String> slider_image;
    private String store_name;
    private String store_info;
    private Object thirdparty_link;
    private Long spec_type;
    private String spu;
    private String code;
    private String keyword;
    private Object unit_name;
    private String price;
    private String market_price;
    private String group_price;
    private String rate;
    private String type;
    private Long status;
    private Long is_overseas;
    private String source_supply;
    private Object info;
    private String channel_name;
    private String type_name;
    private Object after_sale;
    private Category category;
    private Brand brand;
    private List<Sku> sku;
    private AttrResult attr_result;
    private String description;

    @Data
    public static class AttrResult {
        private Attrs attrs;
        private List<AttrResultAttr> attr;
    }

    @Data
    public static class AttrResultAttr {
        private String value;
        private List<String> detail;
    }

    @Data
    public static class Attrs {
        private List<String> attrs;
        private List<String> empty;
    }

    @Data
    public static class Brand {
        private String name;
        private String class_id;
        private String thumbnail_img;
    }

    @Data
    public static class Category {
        private String name;
        private Long parent_id;
    }

    @Data
    public static class Sku {
        private Long sku_id;
        private String suk;
        private String sku;
        private List<SkuAttr> attr;
        private Long stock;
        private String price;
        private String image;
        private String bar_code;
        private String market_price;
        private String group_price;
        private Double weight;
        private String volume;
        private Long is_show;
        private Long buy_start_qty;
        private Double service_price;
        private SkuDesc sku_desc;
    }

    @Data
    public static class SkuAttr {
        private String name;
        private String value;
    }

    @Data
    public static class SkuDesc {
        private String skuname;
        private String description;
    }
}
