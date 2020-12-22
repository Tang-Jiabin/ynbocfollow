package com.zykj.follow.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 微信用户
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */
@Data
@Entity
@Table(name = "boc_yn_wechat_user")
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
public class WechatUserInfo {

    @Id
    @Column(length = 64)
    private String userId;
    /**
     * openid
     */
    @Column(length = 64)
    private String openId;
    /**
     * 昵称
     */
    @Column(length = 64)
    private String nickname;
    /**
     * 过滤后昵称
     */
    @Column(length = 64)
    private String nicknameTxt;
    /**
     * 备注名称
     */
    @Column(length = 64)
    private String noteName;
    /**
     * 用户头像
     */
    @Column(length = 200)
    private String headImgUrl;
    /**
     * 性别
     */
    @Column(length = 64)
    private String sex;
    /**
     * 是否关注:'0':未关注；'1':关注
     */
    @Column(length = 64)
    private String subscribe;
    /**
     * 关注时间
     */
    private Date firstSubscribeTime;
    /**
     * 关注时间
     */
    @Column(length = 64)
    private String subscribeTime;
    /**
     * 用户关注渠道来源
     */
    @Column(length = 64)
    private String subscribeScene;
    /**
     * 手机号
     */
    @Column(length = 64)
    private String mobile;
    /**
     * 活动手机号
     */
    @Column(length = 64)
    private String activityMobile;
    /**
     * 员工手机号
     */
    @Column(length = 64)
    private String staffMobile;

    /**
     * 绑定状态：'0':未绑定；'1':已绑定
     */
    @Column(length = 64)
    private String bindStatus;
    /**
     * 绑定时间
     */
    private Date bindTime;
    /**
     * 标签id
     */
    @Column(length = 64)
    private String tagIdList;
    /**
     * 本地标签Id
     */
    @Column(length = 64)
    private String localTagIdList;

    /**
     * 省份
     */
    @Column(length = 64)
    private String province;
    /**
     * 城市
     */
    @Column(length = 64)
    private String city;
    /**
     * 地区
     */
    @Column(length = 64)
    private String country;
    /**
     * 二维码扫码场景
     */
    @Column(length = 64)
    private String qrScene;
    /**
     * 二维码扫码常见描述
     */
    @Column(length = 64)
    private String qrSceneStr;
    /**
     * 用户所在分组id
     */
    @Column(length = 64)
    private String groupId;
    /**
     * 用户的语言，简体中文为zh_CN
     */
    @Column(length = 64)
    private String language;
    /**
     * unionid
     */
    @Column(length = 64)
    private String unionId;
    /**
     * 公众号原始id
     */
    @Column(length = 64)
    private String jwId;
    /**
     * 创建时间
     */
    private Date createTime;

    @Column(length = 64)
    private String sid;
}
