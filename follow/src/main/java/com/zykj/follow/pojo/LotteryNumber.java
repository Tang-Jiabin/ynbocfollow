package com.zykj.follow.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 抽奖次数
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */

@Data
@Entity
@Table(name = "boc_yn_lottery_number")
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
public class LotteryNumber {

    @Id
    @Column(length = 64)
    private String lotteryId;

    /**
     * 用户ID
     */
    @Column(length = 64)
    private String userId;

    /**
     * 手机号
     */
    @Column(length = 64)
    private String phone;

    /**
     * 邀请总数
     */
    private Integer invitationTotal;

    /**
     * 邀请剩余数 每次加1 遇5清零
     */
    private Integer invitationSurplus;

    /**
     * 总抽奖数量
     */
    private Integer totalSurplusNumber;

    /**
     * 剩余数量
     */
    private Integer surplusNumber;


}
