package com.zykj.follow.utils;

import com.alibaba.fastjson.JSONObject;
import com.bocnet.common.security.PKCS7Tool;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 中银网页支付
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-17
 */

public class BocWebPayUtil {


    public static final String ORDER_NOTE = "中行云南关注活动";
    public static final String ORDER_AMOUNT = "0.01";
    public static final String ORDER_URL = "https://wechat.zhongyunkj.cn/ynbocfollow/staff/payCallback";
    public static final String PAY_TYPE = "1";
    public static final String CUR_CODE = "001";
    public static final String MERCHANT_NO = "104110048995507";
    public static final String KEY_STORE_PATH = "/zykey.pfx";
    public static final String KEY_STORE_PWD = "123456";
    public static final String ACTION = "https://ebspay.boc.cn/PGWPortal/B2CMobileRecvOrder.do";


    /**
     * 中银web支付
     *
     * @param orderNote   订单说明 活动名称
     * @param orderAmount 订单金额
     * @param orderUrl    回调地址
     * @return 支付参数
     * @throws Exception 秘钥文件生成错误
     */
    public static JSONObject createPayParam(String orderNote, String orderAmount, String orderUrl) throws Exception {

        //创建订单日期
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String orderTime = formatter.format(new Date());

        //订单号
        SnowflakeIdFactory factory = new SnowflakeIdFactory(SnowflakeIdFactory.getWorkerId(), 10L);
        formatter = new SimpleDateFormat("yyyyMMdd");
        String orderNo = formatter.format(new Date()) + factory.nextId();

        return createPayParam(PAY_TYPE, CUR_CODE, orderNote, orderAmount, orderTime, orderNo, MERCHANT_NO, KEY_STORE_PATH, KEY_STORE_PWD, orderUrl);
    }


    /**
     * 中银web支付
     *
     * @param payType      支付类型 默认1
     * @param curCode      支付Code 默认001
     * @param orderNote    订单说明 活动名称
     * @param orderAmount  订单金额
     * @param orderTime    订单时间
     * @param orderNo      订单号
     * @param merchantNo   商户号
     * @param keyStorePath 秘钥文件地址  resources文件夹下  例：/key.pfx
     * @param keystorePwd  秘钥文件密码
     * @param orderUrl     回调url
     * @return JSONObject  支付参数
     * @throws Exception 秘钥文件生成错误
     */
    public static JSONObject createPayParam(String payType, String curCode, String orderNote, String orderAmount, String orderTime, String orderNo, String merchantNo, String keyStorePath, String keystorePwd, String orderUrl) throws Exception {

        //秘钥文件
        InputStream keyStore = BocWebPayUtil.class.getClassLoader().getResourceAsStream(keyStorePath);

        //签名
        String plainText = orderNo + "|" + orderTime + "|" + curCode + "|" + orderAmount + "|" + merchantNo;
        byte[] plainTextByte = plainText.getBytes(StandardCharsets.UTF_8);
        PKCS7Tool tool = PKCS7Tool.getSigner(keyStore, KeyStore.getDefaultType(), keystorePwd, keystorePwd);
        String signData = tool.sign(plainTextByte);

        //支付参数
        JSONObject payParam = new JSONObject();
        payParam.put("merchantNo", merchantNo);
        payParam.put("payType", payType);
        payParam.put("orderNo", orderNo);
        payParam.put("curCode", curCode);
        payParam.put("orderAmount", orderAmount);
        payParam.put("orderTime", orderTime);
        payParam.put("orderNote", orderNote);
        payParam.put("orderUrl", orderUrl);
        payParam.put("signData", signData);
        payParam.put("action", ACTION);

        return payParam;
    }

}
