package com.cw.demo.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商务专员表
 *
 * @author thisdcw
 * @TableName saleMan saleMan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleMan {

    private Long id;

    private Integer level;
}