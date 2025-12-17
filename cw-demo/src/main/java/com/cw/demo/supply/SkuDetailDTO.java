package com.cw.demo.supply;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author thisdcw
 * @date 2025年06月11日 13:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SkuDetailDTO extends BaseReq {

    private Integer sku_id;

    private Integer spu_id;
}
