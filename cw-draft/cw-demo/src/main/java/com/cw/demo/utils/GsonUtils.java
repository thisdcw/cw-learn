package com.cw.demo.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author thisdcw
 * @date 2025年03月21日 9:40
 */
public class GsonUtils {

    private static final Gson GSON = new Gson();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }
}
