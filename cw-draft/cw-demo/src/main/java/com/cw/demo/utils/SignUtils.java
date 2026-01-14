package com.cw.demo.utils;

import cn.hutool.core.util.ObjectUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author thisdcw
 * @date 2025年03月27日 17:13
 */
public class SignUtils {


    public static String createSign(Object obj, String secretKey) {
        StringBuilder sb = new StringBuilder();
        // 将参数以参数名的字典升序排序
        HashMap map = JsonUtils.parseObject(JsonUtils.toJson(obj), HashMap.class);
        Map<String, Object> sortParams = new TreeMap<>(map);
        // 遍历排序的字典,并拼接"key=value"格式
        for (Map.Entry<String, Object> entry : sortParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!ObjectUtil.isEmpty(value)) {
                if (value instanceof List) {
                    // 如果值是 List 集合，则自行拼接
                    List<?> list = (List<?>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Object item = list.get(i);
                        if (item instanceof Map) {
                            Map<?, ?> itemMap = (Map<?, ?>) item;
                            for (Map.Entry<?, ?> itemEntry : itemMap.entrySet()) {
                                sb.append("&").append(key).append("[").append(i).append("]").append("[").append(itemEntry.getKey()).append("]").append("=").append(itemEntry.getValue());
                            }
                        } else {
                            // Handle other types within the list if needed
                            sb.append("&").append(key).append("[").append(i).append("]").append("=").append(item);
                        }
                    }
                } else {
                    sb.append("&").append(key).append("=").append(value);
                }
            }
        }

        String stringA = sb.toString().replaceFirst("&", "");
        String stringSignTemp = stringA + secretKey;
//        log.info("生成签名加密前 " + stringSignTemp);
        // 将签名使用MD5加密并全部字母变为大写
//         log.info("生成签名加密后 " + signValue);
        return md5Encrypt(stringSignTemp).toUpperCase();
    }

    public static String md5Encrypt(String input) {
        try {
            // 创建 MessageDigest 实例
            MessageDigest digest = MessageDigest.getInstance("MD5");

            // 使用指定的 byte 数组更新摘要
            digest.update(input.getBytes());

            // 获取摘要的字节数组
            byte[] md5Bytes = digest.digest();

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                String hex = Integer.toHexString(0xff & md5Byte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // 将字符串全部转换为大写
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5计算失败", e);
        }
    }

    public static String HmacSHA256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HmacSHA256计算失败", e);
        }
    }
}
