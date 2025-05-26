package com.cw.safe;

import lombok.Data;

/**
 * @author thisdcw
 * @date 2025年05月21日 16:12
 */
@Data
public class R<T> {

    private int code;

    private String msg;

    private T data;
}
