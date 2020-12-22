package com.zykj.follow.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 领取手机信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-14
 */
@Data
@Entity
@Table(name = "boc_yn_receive_phone_info")
public class ReceivePhoneInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length = 64)
    private Integer infoId;

    @Column(length = 64)
    private String name;

    @Column(length = 64)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(length = 64)
    private Date createDate;
}
