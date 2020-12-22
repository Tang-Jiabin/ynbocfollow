package com.zykj.follow.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 微信用户邀请二维码信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-15
 */
@Data
@Entity
@Table(name = "boc_yn_wechat_user_qr_info")
public class WeChatUserQrInfo {


    @Id
    @Column(length = 64)
    private String qrId;

    @Column(length = 64)
    private String qrPath;

    @Column(length = 64)
    private Date createDate;

    /**
     * 场景ID 1001-用户 1002-员工
     */
    private Integer sceneId;


}
