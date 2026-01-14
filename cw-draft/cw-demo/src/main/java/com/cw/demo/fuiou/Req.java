package com.cw.demo.fuiou;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.XmlUtil;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;

/**
 * @author thisdcw
 * @date 2025年07月28日 9:52
 */
@Data
public class Req {

    private String insCd;

    private String mchntCd;

    private String randomStr;

    private String startDate;

    private String endDate;
    private String sign;

    private Integer startIndex;

    private Integer endIndex;

    private static String marketingSignature(Map<String, Object> map, String singName) {
        String paramArr = MapUtil.join(map, "&", "=", true, new String[0]);
        String signArr = new String(paramArr.getBytes(StandardCharsets.UTF_8), CharsetUtil.CHARSET_GBK);
        String signatureStr = "";

        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64Decoder.decode(SignUtils.PRIVATE_KEY));
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(privateKey);
            signature.update(signArr.getBytes());
            signatureStr = Base64Encoder.encode(signature.sign());
        } catch (Exception var9) {
            Exception e = var9;
            e.printStackTrace();
        }

        map.put(singName, signatureStr);
        return URLUtil.encodeQuery(XmlUtil.mapToXmlStr(map, "xml"), CharsetUtil.CHARSET_GBK);
    }

}
