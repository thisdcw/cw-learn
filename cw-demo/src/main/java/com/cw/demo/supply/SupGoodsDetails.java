package com.cw.demo.supply;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author thisdcw
 * @date 2025年06月11日 10:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SupGoodsDetails extends BaseReq {

    private Integer spu_id;
}
