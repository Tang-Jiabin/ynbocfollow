package com.zykj.follow.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zykj.follow.common.Module;
import com.zykj.follow.common.http.OkhttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;

/**
 * 二维码工具
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-11
 */
@Slf4j
public class QrUtils {


    /**
     * 获取带参二维码
     *
     * @param expireSeconds 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒。
     * @param actionName    二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
     * @param sceneId       场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
     * @param sceneStr      场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
     * @return 二维码链接
     */
    public static String getParamQr(int expireSeconds, String actionName, int sceneId, String sceneStr) {
        JSONObject qrJson = new JSONObject();
        JSONObject actionInfo = new JSONObject();
        JSONObject sceneInfo = new JSONObject();
        actionInfo.put("scene_id", sceneId);
        actionInfo.put("scene_str", sceneStr);
        sceneInfo.put("scene",actionInfo);
        qrJson.put("expire_seconds", expireSeconds);
        qrJson.put("action_name", actionName);
        qrJson.put("action_info", sceneInfo);
        String qrJsonStr = qrJson.toJSONString();

        log.info("qrJsonStr: {}", qrJsonStr);
        String getQrTicketUrl = Module.GET_QR_TICKET.replace("TOKEN", Module.getAccessToken());

        String ticketInfo = OkhttpUtil.getInstance().httpPost(getQrTicketUrl, qrJsonStr);
        log.info("ticketInfo: {}", ticketInfo);

        JSONObject ticketInfoJson = JSON.parseObject(ticketInfo);
        String ticket = ticketInfoJson.getString("ticket");
        if (StringUtils.hasLength(ticket)) {
            String qrUrl = Module.GET_QR_CODE_URL.replace("TICKET", URLEncoder.encode(ticket));
            log.info("qrUrl: {}",qrUrl);
            return qrUrl;
        }

        return "";
    }

}
