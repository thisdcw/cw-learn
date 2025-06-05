package com.cw.framework.common;

import java.io.Serializable;
import java.util.Map;

/**
 * 通用结果封装类
 *
 * @param <T> 数据类型
 */
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功状态码
     */
    public static final int SUCCESS = 0;

    /**
     * 未知错误状态码
     */
    public static final int FAIL = -1;

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * 默认构造方法，返回成功状态
     */
    public ResultBean() {
        this(SUCCESS, "success", null);
    }

    /**
     * 构造方法，只设置数据，状态默认为成功
     *
     * @param data 数据
     */
    public ResultBean(T data) {
        this(SUCCESS, "success", data);
    }

    /**
     * 构造方法，设置状态码和消息，无数据
     *
     * @param code 状态码
     * @param msg  消息
     */
    public ResultBean(int code, String msg) {
        this(code, msg, null);
    }

    /**
     * 构造方法，设置全部属性
     *
     * @param code 状态码
     * @param msg  消息
     * @param data 数据
     */
    public ResultBean(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 创建成功结果
     *
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> ResultBean<T> success() {
        return new ResultBean<>();
    }

    /**
     * 创建带数据的成功结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 成功结果
     */
    public static <T> ResultBean<T> success(T data) {
        return new ResultBean<>(data);
    }

    /**
     * 创建失败结果
     *
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> ResultBean<T> fail(String msg) {
        return new ResultBean<>(FAIL, msg);
    }

    /**
     * 创建带状态码的失败结果
     *
     * @param code 状态码
     * @param msg  错误消息
     * @param <T>  数据类型
     * @return 失败结果
     */
    public static <T> ResultBean<T> fail(int code, String msg) {
        return new ResultBean<>(code, msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
} 