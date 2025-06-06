package com.cw.framework.payment.wechat.v2.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.cw.framework.payment.wechat.v2.config.WxPayV2Properties;
import com.cw.framework.payment.wechat.v2.constant.ParamsKey;
import com.cw.framework.payment.wechat.v2.constant.ResponseState;
import com.cw.framework.payment.wechat.v2.constant.WxUrl;
import com.cw.framework.payment.wechat.v2.enums.TradeType;
import com.cw.framework.payment.wechat.v2.model.OrderQuery;
import com.cw.framework.payment.wechat.v2.model.OrderRequest;
import com.cw.framework.utils.HttpUtil;
import com.cw.framework.utils.WxPayV2Util;
import com.cw.framework.utils.HttpServletUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author thisdcw
 * @date 2025年06月05日 16:38
 */
public class WxPayV2Service {

    private final WxPayV2Properties config;

    public WxPayV2Service(WxPayV2Properties config) {
        this.config = config;
    }

    /**
     * 小程序下单
     *
     * @param request 下单参数
     * @return 支付参数
     * @throws Exception 下单异常
     */
    public String createOrder(OrderRequest request) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put(ParamsKey.APPID, config.getAppId());
        data.put(ParamsKey.MCH_ID, config.getMchId());
        data.put(ParamsKey.NONCE_STR, WxPayV2Util.generateNonceStr());
        data.put(ParamsKey.BODY, request.getBody());
        data.put(ParamsKey.OUT_TRADE_NO, request.getOutTradeNo());
        data.put(ParamsKey.TOTAL_FEE, String.valueOf(request.getTotalFee()));
        data.put(ParamsKey.SP_BILL_CREATE_IP, HttpServletUtils.getCurrentClientIp());
        data.put(ParamsKey.NOTIFY_URL, config.getNotifyUrl());
        data.put(ParamsKey.TRADE_TYPE, TradeType.JSAPI.getType());
        data.put(ParamsKey.OPENID, request.getOpenid());

        String sign = WxPayV2Util.generateSignature(data, config.getMchKey());
        data.put(ParamsKey.SIGN, sign);

        String xml = WxPayV2Util.mapToXml(data);

        // POST 到统一下单接口
        String responseXml = HttpUtil.postXml(WxUrl.CREATE_ORDER, xml);
        Map<String, String> resultMap = WxPayV2Util.xmlToMap(responseXml);

        // 判断是否成功
        if ("SUCCESS".equals(resultMap.get("return_code")) && "SUCCESS".equals(resultMap.get("result_code"))) {
            String prepayId = resultMap.get("prepay_id");
            // 根据 trade_type 返回不同字段，比如 JSAPI 用 prepay_id
            return prepayId;
        } else {
            throw new RuntimeException("微信下单失败：" + resultMap.get("return_msg") + " / " + resultMap.get("err_code_des"));
        }

    }

    /**
     * 查询订单
     *
     * @param query 查询参数
     * @return 结果
     * @throws Exception 查询异常
     */
    public String queryOrder(OrderQuery query) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ParamsKey.APPID, config.getAppId());
        params.put(ParamsKey.MCH_ID, config.getMchId());
        params.put(ParamsKey.OUT_TRADE_NO, query.getOutTradeNo());
        params.put(ParamsKey.NONCE_STR, WxPayV2Util.generateNonceStr());

        params.put(ParamsKey.SIGN, WxPayV2Util.generateSignature(params, config.getMchKey()));
        String requestXml = WxPayV2Util.mapToXml(params);

        return HttpUtil.postXml(WxUrl.QUERY_ORDER, requestXml);
    }

    /**
     * 关闭订单
     *
     * @param query 订单参数
     * @return 结果
     * @throws Exception 关闭订单异常
     */
    public String closeOrder(OrderQuery query) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ParamsKey.APPID, config.getAppId());
        params.put(ParamsKey.MCH_ID, config.getMchId());
        params.put(ParamsKey.OUT_TRADE_NO, query.getOutTradeNo());
        params.put(ParamsKey.NONCE_STR, WxPayV2Util.generateNonceStr());

        params.put(ParamsKey.SIGN, WxPayV2Util.generateSignature(params, config.getMchKey()));
        String requestXml = WxPayV2Util.mapToXml(params);

        return HttpUtil.postXml(WxUrl.CLOSE_ORDER, requestXml);
    }

    /**
     * 生成二维码 URL（Native 支付）
     *
     * @param request 订单参数
     * @return 二维码数据
     * @throws Exception 创建url异常
     */
    public String createPayUrl(OrderRequest request) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ParamsKey.APPID, config.getAppId());
        params.put(ParamsKey.MCH_ID, config.getMchId());
        params.put(ParamsKey.NONCE_STR, WxPayV2Util.generateNonceStr());
        params.put(ParamsKey.BODY, request.getBody());
        params.put(ParamsKey.OUT_TRADE_NO, request.getOutTradeNo());
        params.put(ParamsKey.TOTAL_FEE, String.valueOf(request.getTotalFee()));
        params.put(ParamsKey.SP_BILL_CREATE_IP, HttpServletUtils.getCurrentClientIp());
        params.put(ParamsKey.NOTIFY_URL, config.getNotifyUrl());
        // QR 支付关键参数
        params.put(ParamsKey.TRADE_TYPE, TradeType.NATIVE.getType());

        params.put(ParamsKey.SIGN, WxPayV2Util.generateSignature(params, config.getMchKey()));
        String requestXml = WxPayV2Util.mapToXml(params);

        String responseXml = HttpUtil.postXml(WxUrl.CREATE_ORDER, requestXml);
        Map<String, String> respMap = WxPayV2Util.xmlToMap(responseXml);

        if (ResponseState.SUCCESS.equals(respMap.get(ResponseState.RETURN_CODE)) && ResponseState.SUCCESS.equals(respMap.get(ResponseState.RESULT_CODE))) {
            // 用于生成二维码
            String codeUrl = respMap.get(ParamsKey.CODE_URL);
            return generateQrCode(codeUrl);
        } else {
            throw new RuntimeException("微信下单失败：" + respMap.get(ParamsKey.RETURN_MSG));
        }
    }

    /**
     * 创建H5支付
     *
     * @param request 请求参数
     * @param wapUrl  支付成功跳转的url
     * @param wapName 跳转后的页面标题
     * @return 支付url
     * @throws Exception 异常
     */
    public String createH5PayUrl(OrderRequest request, String wapUrl, String wapName) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ParamsKey.APPID, config.getAppId());
        params.put(ParamsKey.MCH_ID, config.getMchId());
        params.put(ParamsKey.NONCE_STR, WxPayV2Util.generateNonceStr());
        params.put(ParamsKey.BODY, request.getBody());
        params.put(ParamsKey.OUT_TRADE_NO, request.getOutTradeNo());
        params.put(ParamsKey.TOTAL_FEE, String.valueOf(request.getTotalFee()));
        params.put(ParamsKey.SP_BILL_CREATE_IP, HttpServletUtils.getCurrentClientIp());
        params.put(ParamsKey.NOTIFY_URL, config.getNotifyUrl());
        // QR 支付关键参数
        params.put(ParamsKey.TRADE_TYPE, TradeType.H5.getType());
        String sceneInfo = String.format(
                "{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"%s\",\"wap_name\": \"%s\"}}",
                wapUrl, wapName
        );
        params.put(ParamsKey.SCENE_INFO, sceneInfo);

        params.put(ParamsKey.SIGN, WxPayV2Util.generateSignature(params, config.getMchKey()));

        String reqXml = WxPayV2Util.mapToXml(params);
        String respXml = HttpUtil.postXml(WxUrl.CREATE_ORDER, reqXml);
        Map<String, String> resp = WxPayV2Util.xmlToMap(respXml);

        if (ResponseState.SUCCESS.equals(resp.get(ResponseState.RETURN_CODE)) && ResponseState.SUCCESS.equals(resp.get(ResponseState.RESULT_CODE))) {
            // 重定向这个 URL 进行支付
            return resp.get(ParamsKey.M_WEB_URL);
        } else {
            throw new RuntimeException("H5下单失败：" + resp.get(ParamsKey.RETURN_MSG));
        }
    }

    /**
     * 创建APP支付
     *
     * @param request 下单参数
     * @return APP支付参数
     * @throws Exception 异常
     */
    public Map<String, String> createAppPayParams(OrderRequest request) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ParamsKey.APPID, config.getAppId());
        params.put(ParamsKey.MCH_ID, config.getMchId());
        params.put(ParamsKey.NONCE_STR, WxPayV2Util.generateNonceStr());
        params.put(ParamsKey.BODY, request.getBody());
        params.put(ParamsKey.OUT_TRADE_NO, request.getOutTradeNo());
        params.put(ParamsKey.TOTAL_FEE, String.valueOf(request.getTotalFee()));
        params.put(ParamsKey.SP_BILL_CREATE_IP, HttpServletUtils.getCurrentClientIp());
        params.put(ParamsKey.NOTIFY_URL, config.getNotifyUrl());
        params.put(ParamsKey.TRADE_TYPE, TradeType.APP.getType());
        params.put(ParamsKey.SIGN, WxPayV2Util.generateSignature(params, config.getMchKey()));

        String reqXml = WxPayV2Util.mapToXml(params);
        String respXml = HttpUtil.postXml(WxUrl.CREATE_ORDER, reqXml);
        Map<String, String> resp = WxPayV2Util.xmlToMap(respXml);

        if (!ResponseState.SUCCESS.equals(resp.get(ResponseState.RETURN_CODE)) || !ResponseState.SUCCESS.equals(resp.get(ResponseState.RESULT_CODE))) {
            throw new RuntimeException("APP下单失败：" + resp.get(ParamsKey.RETURN_MSG));
        }

        // 组装 APP 端调起参数
        String prepayId = resp.get(ParamsKey.PREPAY_ID);
        Map<String, String> appParams = new HashMap<>();
        appParams.put(ParamsKey.APPID, config.getAppId());
        appParams.put(ParamsKey.APP_PARTNER_ID, config.getMchId());
        appParams.put(ParamsKey.APP_PREPAY_ID, prepayId);
        appParams.put(ParamsKey.APP_PACKAGE, ParamsKey.APP_PACKAGE_VALUE);
        appParams.put(ParamsKey.APP_NONCE_STR, WxPayV2Util.generateNonceStr());
        appParams.put(ParamsKey.APP_TIMESTAMP, String.valueOf(System.currentTimeMillis() / 1000));
        appParams.put(ParamsKey.SIGN, WxPayV2Util.generateSignature(appParams, config.getMchKey()));
        // 前端直接传入 wx api 发起支付
        return appParams;
    }


    private String generateQrCode(String payUrl) throws Exception {
        BufferedImage image = QrCodeUtil.generate(payUrl, 300, 300);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        return ParamsKey.IMG_PRE + Base64.encode(stream.toByteArray());

    }

}

