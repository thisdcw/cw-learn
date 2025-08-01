package com.cw.framework.supply;

import lombok.Data;

/**
 * @author thisdcw
 * @date 2025年06月11日 10:52
 */
@Data
public class BaseReq {

    private String appId;
    private String timestamp;
    private String onceString;
    private String sign;
    private String versions = "v2";
}
