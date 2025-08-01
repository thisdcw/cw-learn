package com.cw.framework.supply;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author thisdcw
 * @date 2025年04月21日 16:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostageDTO extends BaseReq {
    private List<SkuDTO> sku_list;
    private String shipAreaCode;
}
