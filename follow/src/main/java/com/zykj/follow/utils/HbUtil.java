package com.zykj.follow.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class HbUtil {

    @Autowired
    private RedisUtil redisUtil;
    /**
     * 领取红包
     */
    public String hb(String openId,String money)  {
        String data="";
        data = "{\"Code\":\"5\",\"returnCode\":\"您已经领过红包了！\"}";
        String appUserId = openId;
        String appid = "wx0b3ba702df4d46df";
        String param = "{\"State\":\"SzRKYU1Mbk56aVh1aXBOOEYxTlMwdz09\",\"AppId\":\"" + appid
                + "\",\"FromAppName\":\"保定银行\",\"Name\":\"保定银行推广活动\",\"NickName\":\"保定银行\",\"OpenId\":\""
                + openId + "\",\"PriceVal\":\"" + money + "\",\"Remark\":\"" + appUserId
                + "\",\"SendConfigId\":\"464a7d15-642f-49a9-82a8-5a3df8f698c3\",\"SendType\":\"红包\",\"Title\":\"保定银行推广活动红包\"}";

        String url = "http://www.xiaozaixiao.com/xiaozaixiaoapi/pushredpack";
        try{
            byte[] requestStr = HttpUtil.doPost(url,param);
            String jsonStr = new String(requestStr,"UTF-8");
            JSONObject requestJson = JSONObject.parseObject(jsonStr);
            String Code = requestJson.getString("Code");
            String Msg = requestJson.getString("Msg");
            if (Code.equals("1")) {
                data = "{\"Code\":\"4\",\"returnCode\":\"红包余额不足，请联系服务商！\"}";
            } else if (Code.equals("200")) {
                data = "{\"Code\":\"1\",\"returnCode\":\"红包领取成功，请注意查收！\",\"Msg\":\"" + Msg + "\"}";
            } else if (Code.equals("302")) {
                data = "{\"Code\":\"2\",\"returnCode\":\"红包领取成功，请注意查收！\",\"Msg\":\"" + Msg + "\"}";
            }else{
                data = "{\"Code\":\"3\",\"returnCode\":\"红包领取失败，请联系服务商！\"}";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
}
