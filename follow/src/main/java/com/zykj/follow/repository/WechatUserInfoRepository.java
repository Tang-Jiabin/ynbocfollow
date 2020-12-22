package com.zykj.follow.repository;

import com.zykj.follow.pojo.WechatUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tang
 */
@Repository
public interface WechatUserInfoRepository extends JpaRepository<WechatUserInfo, String> {
    WechatUserInfo findByOpenId(String openid);

    WechatUserInfo findBySid(String sid);

    WechatUserInfo findByActivityMobile(String tokenPhone);

    WechatUserInfo findByUserId(String userId);

    WechatUserInfo findByStaffMobile(String phone);

    List<WechatUserInfo> findAllByUserIdIn(List<String> acceptUserIdList);
}
