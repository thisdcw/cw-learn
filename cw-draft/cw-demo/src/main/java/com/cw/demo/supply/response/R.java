package com.cw.demo.supply.response;

import lombok.Data;

/**
 * @author thisdcw
 * @date 2025年04月21日 16:59
 */
@Data
public class R<T> {

    private String status;
    private Integer code;
    private String message;
    private T data;

}
