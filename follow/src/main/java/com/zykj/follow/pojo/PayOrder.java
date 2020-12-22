package com.zykj.follow.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * 支付订单
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-17
 */

@Data
@Entity
@Table(name = "boc_yn_pay_order")
public class PayOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer payId;

    @Column(length = 64)
    private String inviteUserId;
    @Column(length = 64)
    private String acceptUserId;
    @Column(length = 64)
    private String orderNo;
    @Column(length = 64)
    private String merchantNo;
    @Column(length = 64)
    private String orderSeq;
    @Column(length = 64)
    private String cardTyp;
    @Column(length = 64)
    private String payTime;
    @Column(length = 64)
    private String orderStatus;
    @Column(length = 64)
    private String payAmount;
    @Column(length = 64)
    private String ibknum;
    @Column(length = 64)
    private String phoneNum;
    @Column(length = 64)
    private String signData;
    @Column(length = 64)
    private String acctNo;
    @Column(length = 64)
    private String holderName;
}
