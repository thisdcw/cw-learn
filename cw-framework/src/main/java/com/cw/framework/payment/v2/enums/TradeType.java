package com.cw.framework.payment.v2.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author thisdcw
 * @date 2025年06月05日 20:38
 */
@AllArgsConstructor
@Getter
public enum TradeType {
    JSAPI("JSAPI"),
    NATIVE("NATIVE"),
    H5("MWEB"),
    APP("APP"),
    ;
    private final String type;
}
