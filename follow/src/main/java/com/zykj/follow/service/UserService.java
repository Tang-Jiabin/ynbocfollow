package com.zykj.follow.service;

import com.alibaba.fastjson.JSONObject;
import com.vdurmont.emoji.EmojiParser;
import com.zykj.follow.pojo.WechatUserInfo;
import com.zykj.follow.repository.WechatUserInfoRepository;
import com.zykj.follow.utils.SnowflakeIdFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */
@Slf4j
@Service
public class UserService {

    private final WechatUserInfoRepository userRepository;

    public UserService(WechatUserInfoRepository userRepository) {
        this.userRepository = userRepository;
    }


    public WechatUserInfo findByOpenId(String openid) {
        return userRepository.findByOpenId(openid);
    }

    public synchronized WechatUserInfo save(JSONObject wxUserInfo) {

        WechatUserInfo weiXinUser;

        String openid = wxUserInfo.getString("openid");
        if (!StringUtils.hasLength(openid)) {
            return null;
        }
        weiXinUser = userRepository.findByOpenId(openid);
        if (weiXinUser == null) {
            SnowflakeIdFactory snowflakeIdFactory = new SnowflakeIdFactory(1L, 1L);
            String userId = String.valueOf(snowflakeIdFactory.nextId());
            weiXinUser = new WechatUserInfo();
            weiXinUser.setUserId(userId);
            weiXinUser.setFirstSubscribeTime(new Date());

        }

        if (StringUtils.hasLength(openid)) {
            weiXinUser.setOpenId(openid);
        }

        String nickname = wxUserInfo.getString("nickname");
        if (StringUtils.hasLength(nickname)) {
            weiXinUser.setNickname(nickname);
        }

        String headImgUrl = wxUserInfo.getString("headimgurl");
        if (StringUtils.hasLength(headImgUrl)) {
            weiXinUser.setHeadImgUrl(headImgUrl);
        }

        String sid = wxUserInfo.getString("sid");
        if (StringUtils.hasLength(sid)) {
            weiXinUser.setSid(sid);
        }

        String nicknameTxt = wxUserInfo.getString("nicknameTxt");
        if (StringUtils.hasLength(nicknameTxt)) {
            weiXinUser.setNicknameTxt(EmojiParser.removeAllEmojis(nicknameTxt));
        }

        String noteName = wxUserInfo.getString("noteName");
        if (StringUtils.hasLength(noteName)) {
            weiXinUser.setNoteName(noteName);
        }

        String sex = wxUserInfo.getString("sex");
        if (StringUtils.hasLength(sex)) {
            weiXinUser.setSex(sex);
        }

        String subscribe = wxUserInfo.getString("subscribe");
        if (StringUtils.hasLength(subscribe)) {
            weiXinUser.setSubscribe(subscribe);
        }

        String subscribeTime = wxUserInfo.getString("subscribeTime");
        if (StringUtils.hasLength(subscribeTime)) {
            weiXinUser.setSubscribeTime(subscribeTime);
        }

        String subscribeScene = wxUserInfo.getString("subscribeScene");
        if (StringUtils.hasLength(subscribeScene)) {
            weiXinUser.setSubscribeScene(subscribeScene);
        }

        String mobile = wxUserInfo.getString("mobile");
        if (StringUtils.hasLength(mobile)) {
            weiXinUser.setMobile(mobile);
        }

        String bindStatus = wxUserInfo.getString("bindStatus");
        if (StringUtils.hasLength(bindStatus)) {
            weiXinUser.setBindStatus(bindStatus);
        }

        String bindTime = wxUserInfo.getString("bindTime");
        if (StringUtils.hasLength(bindTime)) {

            weiXinUser.setBindTime(new Date());
        }

        String tagIdList = wxUserInfo.getString("tagIdList");
        if (StringUtils.hasLength(tagIdList)) {
            weiXinUser.setTagIdList(tagIdList);
        }

        String localTagIdList = wxUserInfo.getString("localTagIdList");
        if (StringUtils.hasLength(localTagIdList)) {
            weiXinUser.setLocalTagIdList(localTagIdList);
        }

        String province = wxUserInfo.getString("province");
        if (StringUtils.hasLength(province)) {
            weiXinUser.setProvince(province);
        }

        String city = wxUserInfo.getString("city");
        if (StringUtils.hasLength(city)) {
            weiXinUser.setCity(city);
        }

        String country = wxUserInfo.getString("country");
        if (StringUtils.hasLength(country)) {
            weiXinUser.setCountry(country);
        }

        String qrScene = wxUserInfo.getString("qrScene");
        if (StringUtils.hasLength(qrScene)) {
            weiXinUser.setQrScene(qrScene);
        }

        String qrSceneStr = wxUserInfo.getString("qrSceneStr");
        if (StringUtils.hasLength(qrSceneStr)) {
            weiXinUser.setQrSceneStr(qrSceneStr);
        }

        String groupId = wxUserInfo.getString("groupId");
        if (StringUtils.hasLength(groupId)) {
            weiXinUser.setGroupId(groupId);
        }

        String language = wxUserInfo.getString("language");
        if (StringUtils.hasLength(language)) {
            weiXinUser.setLanguage(language);
        }

        String unionId = wxUserInfo.getString("unionId");
        if (StringUtils.hasLength(unionId)) {
            weiXinUser.setUnionId(unionId);
        }
        String jwId = wxUserInfo.getString("jwId");
        if (StringUtils.hasLength(jwId)) {
            weiXinUser.setJwId(jwId);
        }
        weiXinUser.setCreateTime(new Date());

        return userRepository.save(weiXinUser);
    }

    public WechatUserInfo save(WechatUserInfo userInfo) {
        return userRepository.save(userInfo);
    }

    public WechatUserInfo findBySid(String sid) {
        return userRepository.findBySid(sid);
    }

    public WechatUserInfo findByActivityMobile(String tokenPhone) {
        return userRepository.findByActivityMobile(tokenPhone);
    }

    public Optional<WechatUserInfo> findByUserId(String userId) {

        return userRepository.findById(userId);
    }

    public WechatUserInfo findByStaffMobile(String phone) {
        return userRepository.findByStaffMobile(phone);
    }

    public List<WechatUserInfo> findAllByUserIdIn(List<String> acceptUserIdList) {
        return userRepository.findAllByUserIdIn(acceptUserIdList);
    }
}
