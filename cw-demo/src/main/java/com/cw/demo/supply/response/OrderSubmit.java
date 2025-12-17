package com.cw.demo.supply.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author thisdcw
 * @date 2025年04月22日 9:43
 */
@Data
public class OrderSubmit {

    private String order_sn;

    private List<SkuList> sku_list;

    private BigDecimal price;

    private BigDecimal postage;

    private BigDecimal pay_price;

    private BigDecimal service_money;


    @Data
    public static class SkuList {

        private Integer sku_id;
        private String sku;
        private Integer sku_num;
        private BigDecimal unit_price;
    }
}
