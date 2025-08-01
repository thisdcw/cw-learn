package com.cw.framework.fuiou;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

/**
 * @author thisdcw
 * @date 2025年07月28日 9:35
 */
public class SignUtils {

    public static final String PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw2vApdDdgxFT1Z/Jt/SYRwJrfxLY3MncGePuwWt4miOtzacQymNguv1Mx1lkF0wIp/yb7Qi3/Cv" +
                    "w0difLx/9LS7GXHvzC0ar4fF4CT2HHAM4bXtwvhxdRmcbgO4WPgIeR3YwJm1U2+eZLruzGYGvNr5Xd5Jhb34sgHwuflhNhe7ZECS6t0o/f3FAyVH/8bFUUM" +
                    "NMadEIXBaMJyWvIAixHA6QU7UcPxnvn6on0GcFHc4OnXRc1COYqW7e08OwgJ91b1rrOo2kaGrpba2Xw2Ko0f5kMEGvJN9+XXQaAhD2rtUGyiPR/tDKBDahC" +
                    "zGTjxtdhtIZA7vts6KJd2RWB5MdzwIDAQAB";

    public static final String PRIVATE_KEY =
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDDa8Cl0N2DEVPVn8m39JhHAmt/EtjcydwZ4+7Ba3iaI63NpxDKY2C6/UzHWWQXTAin/Jv" +
                    "tCLf8K/DR2J8vH/0tLsZce/MLRqvh8XgJPYccAzhte3C+HF1GZxuA7hY+Ah5HdjAmbVTb55kuu7MZga82vld3kmFvfiyAfC5+WE2F7tkQJLq3Sj9/cUDJUf" +
                    "/xsVRQw0xp0QhcFownJa8gCLEcDpBTtRw/Ge+fqifQZwUdzg6ddFzUI5ipbt7Tw7CAn3VvWus6jaRoaultrZfDYqjR/mQwQa8k335ddBoCEPau1QbKI9H+0" +
                    "MoENqELMZOPG12G0hkDu+2zool3ZFYHkx3PAgMBAAECggEAOzs1sQyVl1xxJJbB9VfVr/Az1NhJkuI8LDzB6S5B57ZYv7SbijyE3ShZVTLq+4S4OvJ98GJs" +
                    "IarP20rlNMtlWYSL6wxGpmxNPLaLw4E6qmrvd3+qtHS0cNpfo7yGUVUjCFXyUjNhPSX9cATETD7adKVMrZlYnwqHQophLTpmhOlP4AhAieUmB2E4moqPesd" +
                    "VKxtP6Sl7npisKitHOYYZo0quIE8TZOq+m5vcJQY42DAWKecEN2UREzGfkVg4XSl4E2A6lg1Jvi6Qsz1RXU6bovrl/hKDoRPIJT1+vjlbu5/vLpB+vukPA8" +
                    "k/zAEuIwM4zsL/gYYbZyP6oZ6I9AgqwQKBgQD4G3/1fk8BN1LCGYLo3zaalOXU8odtFOm23eUkEgXG4PpTnhhyj+kkXDMbhC6zzkWr/h3qbuxg2gYwZKFg3" +
                    "LSz0vQR6phHb0yTPAyYqJDIO40BsRadfTjVQI1G64BOH5gEojdfTZ5yaoC2hGl2/J8SQBfMul9RdNc+7cXKAt76IwKBgQDJozEvzSrggSTIxJg23JKlSdRA" +
                    "dJbEhHcHxOCkJkZNZWXf+6dWbihluu1pSJS5Qc7teVRouW7Ij4336C1sitb/GZADMYuVh+jXdZkZ+vZCg2NZuHxkz6lttqfhrjm4EEYLjrg13sUU70f21La" +
                    "o8R1kTeVQP83a3PWKLpDnif+6ZQKBgQDJKQLGeHJ1+fUPir+pL9GF49O0pC9JMfuMWLzAUhx880wDH9uShzvwTxDg7nsvLb8vhAmdWfItVJhQBEcT0d5xLk" +
                    "jVfe+Ze5QKjFVqMMGylxXsmThM2IqL3JqvNaIBtXv7xco/ax9chR123kTkRE507aJeS32c/a7HRuFmuI0xIQKBgQCjamkgrozDaqbfN8WNIPKQluc4XcUa+" +
                    "6hkWYEeSA7OBxGrl46lE54V5IsourQd+GG2QJDSVwab8QxEEJ1PzK42DQeULwFLbNyeUiaE0cnnWgiY3FWkdlCAJHqnAsawkC/UihRtRGBBeTO/reFjxhXm" +
                    "BuOESFGTwWQWW2sA2+yvqQKBgFFRm99VkPB9c+VmsfYNu1j7y3kVGzwPyQmehiD59AlVwZu9ncVWS9Q0lYB13xFj9nF0bmz5POCd1elyziHVzruTMRF7lYg" +
                    "J9zNlQDcVaWkxwe4LPca9VBwtXgnMERFmwh3c5xX69Cgm2Uz/qKgfIK/DK8mP1cy1pX1V2pmmD23L";

    private static final RSA PAYMENT_RSA = new RSA(PUBLIC_KEY);


    public static String paymentRsaSignature(String str, KeyType keyType) {
        switch (keyType) {
            case PrivateKey:
                return PAYMENT_RSA.decryptStr(str, keyType, CharsetUtil.CHARSET_GBK);
            case PublicKey:
                return PAYMENT_RSA.encryptBase64(str, CharsetUtil.CHARSET_GBK, keyType);
            default:
                return "";
        }
    }
}
