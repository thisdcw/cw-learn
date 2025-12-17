package com.cw.demo.utils;


import okhttp3.*;

import java.util.concurrent.TimeUnit;

/**
 * @author thisdcw
 * @date 2025年06月05日 16:48
 */
public class HttpUtil {

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public static String postXml(String url, String xmlData) throws Exception {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/xml; charset=utf-8"),
                xmlData
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/xml; charset=UTF-8")
                .addHeader("User-Agent", "Mozilla/5.0")
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
            return "";
        }
    }

}
