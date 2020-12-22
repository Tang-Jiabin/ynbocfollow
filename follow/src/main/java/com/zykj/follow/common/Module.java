package com.zykj.follow.common;


import com.alibaba.fastjson.JSONObject;
import com.zykj.follow.common.http.HttpUtils;
import com.zykj.follow.common.http.OkhttpUtil;
import com.zykj.follow.utils.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @author tang
 */
@Slf4j
public class Module {

    public static final String USER = "user";
    public static final String STAFF = "staff";
    public static final String LEADER = "leader";
    public static final String PAY = "pay";



    public static final String APP_ID = "wxe14bbe2f8956c929";
    public static final String SECRET = "84919323dcf16e15cd5dc9c16220d371";
    public static final String GET_QR_TICKET = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN";
    public static final String BACK_URL = "https://wechat.zhongyunkj.cn/ynbocfollow/";
    public static final String OAUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?";
    public static final String ACCESS_TOKEN_URL = " https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APP_ID + "&secret=" + SECRET + "&code=%s&grant_type=authorization_code";
    public static final String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
    public static final String GET_ACCESS_TOKEN = "http://wechat.zhongyunkj.cn/zykj_wx/weixin/back/weixinGYS/authentication/getAccessToken";
    public static final String GET_QR_CODE_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";
    public static final String GET_LJJ_AUTH_URL = "https://wechat.zhongyunkj.cn/wx_ljj/v3/getCoupon?amountType=TYPE&voucherKey=KEY";
    public static final String GET_LJJ_URL = "https://wechat.zhongyunkj.cn/wx_ljj/v3/couponSend?voucherDetilsKey=KEY";
    public static final String LJJ_KEY = "uTjOkNtmFrd1mn7trM8tUCvLG9UzXj2G";
    public static final String ADD_SCENE_QR_CODE = "ADD_SCENE_QR_CODE";


    /**
     * 获取 accessToken
     *
     * @return accesstoken
     */
    public static String getAccessToken() {
        String accessToken = OkhttpUtil.getInstance().httpGet(Module.GET_ACCESS_TOKEN);
        log.info("accessToken: {}", accessToken);
        JSONObject accessTokenJson = JSONObject.parseObject(accessToken);
        accessToken = accessTokenJson.getString("data");
        try {
            accessToken = AESUtil.Decrypt(accessToken, AESUtil.AES_SKEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        accessTokenJson = JSONObject.parseObject(accessToken);
        accessToken = accessTokenJson.getString("accessToken");
        return accessToken;
    }

    @NotNull
    public static JSONObject getWeChatUserInfoJson(String code, String state) {
        log.info("code：" + code);
        log.info("state：" + state);
        String accessTokenUrl = String.format(Module.ACCESS_TOKEN_URL, code);

        String accessTokenResult = HttpUtils.requestAsHttpGET(accessTokenUrl);

        log.info("access_token_result:{}", accessTokenResult);

        JSONObject accessTokenResultJsonObject = JSONObject.parseObject(accessTokenResult);

        String accessToken = Module.getAccessToken();

        String userInfoUrl = String.format(Module.USER_INFO_URL, accessToken, accessTokenResultJsonObject.get("openid"));

        String userInfoUrlResult = HttpUtils.requestAsHttpGET(userInfoUrl);

        log.info("userInfoUrlResult:{}", userInfoUrlResult);
        JSONObject wxUserInfo = JSONObject.parseObject(userInfoUrlResult);
        JSONObject json = JSONObject.parseObject(state);

        String sid = json.getString("sid");
        wxUserInfo.put("sid", sid);
        return wxUserInfo;
    }

}
