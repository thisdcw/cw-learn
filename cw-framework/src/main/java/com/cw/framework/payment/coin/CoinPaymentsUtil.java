package com.cw.framework.payment.coin;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author thisdcw
 * @date 2025年06月06日 14:26
 */
public class CoinPaymentsUtil {

    public static String hmacSha512(String value, String key) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(value.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : macData) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
