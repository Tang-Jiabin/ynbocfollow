package com.zykj.follow.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 邀请记录
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-15
 */
@Data
@Entity
@Table(name = "boc_yn_invitation_record")
public class InvitationRecord {

    @Id
    @Column(length = 64)
    private String inviteId;

    /**
     * 接受邀请日期
     */
    private Date acceptDate;

    /**
     * 邀请人
     */
    @Column(length = 64)
    private String inviteUserId;

    /**
     * 被邀请人
     */
    @Column(length = 64)
    private String acceptUserId;

    /**
     * 邀请场景 1001-用户邀请  1002-员工邀请
     */
    @Column(length = 64)
    private String qrScene;

    /**
     * 邀请状态 1-已关注 2-已领取立减金 3-已支付
     */
    private Integer inviteStatus;
}
