package com.cw.demo.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.cw.demo.supply.response.R;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author thisdcw
 * @date 2025年04月22日 15:17
 */
public class SupplyUtils {

    public static final Logger log = LoggerFactory.getLogger(SupplyUtils.class);

    public static <T> T post(String url, String body, Class<T> tClass) {
        try {
            String response = HttpUtil.createPost(url)
                    .body(body).execute().body();
            log.info("response: {}", response);
            // 先解析 R 的结构
            Type type = TypeToken.getParameterized(R.class, tClass).getType();
            R<T> res = GsonUtils.fromJson(response, type);
            log.info("供应链参数: {}", res);
            if (res.getCode() != 200) {
                System.out.println("异常");
            }
            // 手动反序列化 data 字段
            String dataJson = GsonUtils.toJson(res.getData());
            return GsonUtils.fromJson(dataJson, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T get(String url, Map<String, Object> formData, Class<T> tClass) {
        try {
            log.info("请求参数: {}", formData.toString());
            HttpRequest get = HttpUtil.createGet(url);
            formData.forEach(get::form);
            String response = get.execute().body();

            log.info("response: {}", response);
            // 先解析 R 的结构
            Type type = TypeToken.getParameterized(R.class, tClass).getType();
            R<T> res = GsonUtils.fromJson(response, type);
            log.info("供应链参数: {}", res);
            if (res.getCode() != 200) {
                System.out.println("异常");
            }
            // 手动反序列化 data 字段
            String dataJson = GsonUtils.toJson(res.getData());
            return GsonUtils.fromJson(dataJson, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
