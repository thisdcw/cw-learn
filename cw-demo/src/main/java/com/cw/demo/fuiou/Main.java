package com.cw.demo.fuiou;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author thisdcw
 * @date 2025年07月28日 9:28
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Req req = new Req();
        req.setInsCd("08M0032642");
        req.setRandomStr("oszzR0tS8iipDbY6BMaje4NrjsQF6D5Z");
        req.setStartDate("20240101");
        req.setEndDate("20250701");
        req.setStartIndex(1);
        req.setEndIndex(10);
        req.setMchntCd("0003310F7797392");
        TreeMap<String, Object> treeMap = new TreeMap<>(BeanUtil.beanToMap(req, true, true));
        System.out.println(treeMap);
        String s1 = marketingSignature(treeMap, "sign");
        System.out.println("请求: \n" + URLUtil.decode(s1, CharsetUtil.CHARSET_GBK));

        HttpResponse response = HttpRequest.post("https://scan-rim-mc.fuioupay.com/queryChnlPayAmt")
                .contentType("application/x-www-form-urlencoded")
                .form("req", s1)
                .execute();

        String result = response.body();

        System.out.println("响应:\n" + URLUtil.decode(result, CharsetUtil.CHARSET_GBK));
    }

    private static String marketingSignature(Map<String, Object> map, String singName) {
        // 生成参数字符串用于签名
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("签名: " + signatureStr);

        // 将签名添加到map中
        map.put(singName, signatureStr);

        // 使用GBK编码进行URL编码
        return URLUtil.encodeQuery(XmlUtil.mapToXmlStr(map, "xml"), CharsetUtil.CHARSET_GBK);
    }

}
