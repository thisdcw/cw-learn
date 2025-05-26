package com.cw.safe.entity;

import lombok.Data;

/**
 * @author thisdcw
 * @date 2025年05月19日 21:21
 */
@Data
public class EncryptedRequest {


    // 使用RSA加密后的AES密钥
    private String encryptedKey;

    // 使用Base64编码的AES初始化向量
    private String iv;

    // 使用AES加密后的业务数据
    private String encryptedData;

    // 可选：时间戳，用于防重放攻击
    private Long timestamp;

    // 可选：签名，用于验证请求完整性
    private String signature;


}
