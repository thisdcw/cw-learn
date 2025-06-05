package com.cw.framework.payment.v3.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cw.framework.payment.v3.config.WxProperties;
import com.cw.framework.payment.v3.model.MiniProgramPayRequest;
import com.cw.framework.payment.v3.model.MiniProgramPayResponse;
import com.cw.framework.payment.v3.model.PaymentNotification;
import com.cw.framework.payment.v3.model.RefundRequest;
import com.cw.framework.payment.v3.service.PayService;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.QueryByOutRefundNoRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

/**
 * @author thisdcw
 * @date 2025年06月05日 15:01
 */
@Service
public class PayServiceImpl implements PayService {


    private static final Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);

    @Resource
    private JsapiService jsapiService;

    @Resource
    private RefundService refundService;

    @Resource
    private WxProperties wxProperties;

    private NotificationParser notificationParser;

    @Override
    public MiniProgramPayResponse createOrder(MiniProgramPayRequest orderRequest) {
        try {
            // 构建支付请求
            PrepayRequest request = new PrepayRequest();
            request.setAppid(wxProperties.getAppId());
            request.setMchid(wxProperties.getMchId());
            request.setDescription(orderRequest.getDescription());
            request.setOutTradeNo(orderRequest.getOutTradeNo());
            request.setNotifyUrl(wxProperties.getNotifyUrl());

            // 设置订单金额（单位：分）
            Amount amount = new Amount();
            amount.setTotal(orderRequest.getTotal());
            amount.setCurrency("CNY");
            request.setAmount(amount);

            // 设置支付者信息
            Payer payer = new Payer();
            payer.setOpenid(orderRequest.getOpenid());
            request.setPayer(payer);

            // 设置过期时间（可选）
            if (StringUtils.isNotBlank(orderRequest.getTimeExpire())) {
                request.setTimeExpire(orderRequest.getTimeExpire());
            }

            // 发起统一下单
            PrepayResponse response = jsapiService.prepay(request);

            // 构建小程序支付参数
            return buildMiniProgramPayParams(response.getPrepayId());

        } catch (Exception e) {
            logger.error("创建微信支付订单失败", e);
            throw new RuntimeException("创建支付订单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Transaction queryOrder(String outTradeNo) {
        try {
            QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
            request.setMchid(wxProperties.getMchId());
            request.setOutTradeNo(outTradeNo);

            return jsapiService.queryOrderByOutTradeNo(request);
        } catch (Exception e) {
            logger.error("查询微信支付订单失败, outTradeNo: {}", outTradeNo, e);
            throw new RuntimeException("查询订单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void closeOrder(String outTradeNo) {
        try {
            CloseOrderRequest request = new CloseOrderRequest();
            request.setMchid(wxProperties.getMchId());
            request.setOutTradeNo(outTradeNo);

            jsapiService.closeOrder(request);
            logger.info("关闭订单成功, outTradeNo: {}", outTradeNo);
        } catch (Exception e) {
            logger.error("关闭微信支付订单失败, outTradeNo: {}", outTradeNo, e);
            throw new RuntimeException("关闭订单失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Refund createRefund(RefundRequest refundRequest) {
        try {
            CreateRequest request = new CreateRequest();
            request.setOutRefundNo(refundRequest.getOutRefundNo());
            request.setReason(refundRequest.getReason());
            request.setNotifyUrl(wxProperties.getNotifyUrl());

            // 设置订单号（优先使用微信订单号）
            if (StringUtils.isNotBlank(refundRequest.getTransactionId())) {
                request.setTransactionId(refundRequest.getTransactionId());
            } else {
                request.setOutTradeNo(refundRequest.getOutTradeNo());
            }

            // 设置退款金额
            com.wechat.pay.java.service.refund.model.AmountReq amount =
                    new com.wechat.pay.java.service.refund.model.AmountReq();
            amount.setRefund(refundRequest.getRefund().longValue());
            amount.setTotal(refundRequest.getTotal().longValue());
            amount.setCurrency("CNY");
            request.setAmount(amount);

            return refundService.create(request);
        } catch (Exception e) {
            logger.error("申请微信退款失败", e);
            throw new RuntimeException("申请退款失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Refund queryRefund(String outRefundNo) {
        try {
            QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
            request.setOutRefundNo(outRefundNo);

            return refundService.queryByOutRefundNo(request);
        } catch (Exception e) {
            logger.error("查询微信退款失败, outRefundNo: {}", outRefundNo, e);
            throw new RuntimeException("查询退款失败: " + e.getMessage(), e);
        }
    }

    @Override
    public MiniProgramPayResponse buildMiniProgramPayParams(String prepayId) {
        String timeStamp = String.valueOf(Instant.now().getEpochSecond());
        String nonceStr = generateNonceStr();
        String packageStr = "prepay_id=" + prepayId;

        // 构建签名原串
        String signStr = wxProperties.getAppId() + "\n" + timeStamp + "\n" + nonceStr + "\n" + packageStr + "\n";

        // 生成签名（使用RSA签名）
        String paySign = generatePaySign(signStr);

        MiniProgramPayResponse response = new MiniProgramPayResponse();
        response.setTimeStamp(timeStamp);
        response.setNonceStr(nonceStr);
        response.setPackageValue(packageStr);
        response.setSignType("RSA");
        response.setPaySign(paySign);

        return response;
    }

    /**
     * 生成微信支付V3签名
     *
     * @param signStr 签名原串
     * @return Base64编码的签名
     */
    private String generatePaySign(String signStr) {
        try {
            // 获取商户私钥
            PrivateKey privateKey = getPrivateKey();

            // 使用SHA256withRSA算法签名
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(signStr.getBytes(StandardCharsets.UTF_8));

            // 生成签名并进行Base64编码
            byte[] signBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signBytes);

        } catch (Exception e) {
            logger.error("生成微信支付签名失败", e);
            throw new RuntimeException("生成微信支付签名失败", e);
        }
    }

    /**
     * 获取商户私钥
     *
     * @return 私钥对象
     */
    private PrivateKey getPrivateKey() {
        try {
            // 从文件路径读取私钥内容
            String privateKeyContent = Files.readString(Paths.get(wxProperties.getPrivateKeyPath()), StandardCharsets.UTF_8);

            // 移除PEM格式的头尾标识和换行符
            privateKeyContent = privateKeyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            // Base64解码
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);

            // 生成私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            logger.error("加载商户私钥失败", e);
            throw new RuntimeException("加载商户私钥失败", e);
        }
    }

    @Override
    public String generateNonceStr() {
        return IdUtil.fastSimpleUUID().substring(0, 16);
    }

    @Override
    public boolean verifyNotifySignature(String timestamp, String nonce, String body, String signature) {
        try {
            // 参数校验
            if (StrUtil.hasBlank(timestamp, nonce, body, signature)) {
                logger.warn("微信支付回调参数不完整");
                return false;
            }

            // 构建请求参数
            RequestParam requestParam = new RequestParam.Builder()
                    // 微信支付平台证书序列号
                    .serialNumber(getWechatPaySerialNumber())
                    .nonce(nonce)
                    .signature(signature)
                    .timestamp(timestamp)
                    .body(body)
                    .build();

            try {
                // 使用SDK验证签名并解析通知
                Transaction transaction = notificationParser.parse(requestParam, Transaction.class);
                logger.info("微信支付回调验签成功，订单号：{}", transaction.getOutTradeNo());
                return true;
            } catch (Exception e) {
                logger.error("微信支付回调验签失败", e);
                return false;
            }

        } catch (Exception e) {
            logger.error("验证微信支付回调签名异常", e);
            return false;
        }
    }

    @Override
    public PaymentNotification parsePaymentNotification(String notifyBody) {
        try {
            return JSONUtil.toBean(notifyBody, PaymentNotification.class);
        } catch (Exception e) {
            logger.error("解析微信支付回调通知失败", e);
            throw new RuntimeException("解析支付回调失败", e);
        }
    }

    /**
     * 获取微信支付平台证书序列号
     * 实际项目中应该从配置或缓存中获取
     */
    private String getWechatPaySerialNumber() {
        // 这里应该返回微信支付平台证书的序列号
        // 可以通过API获取或者配置文件设置
        return getLatestWechatPayCertificateSerialNumber();
    }

    /**
     * 获取最新的微信支付平台证书序列号
     * 实际实现中可以缓存证书信息，定期更新
     */
    private String getLatestWechatPayCertificateSerialNumber() {
        try {
            // 这里可以实现获取微信支付平台证书序列号的逻辑
            // 示例：从微信支付获取平台证书列表，取最新的证书序列号

            // 临时返回一个示例值，实际使用时需要替换为真实逻辑
            return "5157F09EFDC096DE15EBE81A47057A7232F1B8E1";
        } catch (Exception e) {
            logger.error("获取微信支付平台证书序列号失败", e);
            throw new RuntimeException("获取平台证书序列号失败", e);
        }
    }
}
