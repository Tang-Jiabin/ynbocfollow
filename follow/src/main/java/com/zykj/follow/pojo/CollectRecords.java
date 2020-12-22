package com.zykj.follow.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zykj.follow.common.PrizeType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 领取记录
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */
@Data
@Entity
@Table(name = "boc_yn_collect_records")
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
public class CollectRecords {

    @Id
    @Column(length = 64)
    private String recordsId;

    @Column(length = 64)
    private String userId;

    @Column(length = 64)
    private String phone;

    @Column(length = 64)
    private String prizeName;

    @Column(length = 64)
    private String prizeImg;

    @Column(length = 64)
    private PrizeType type;

    /**
     * 中奖日期
     */
    @Column(length = 64)
    private Date recordsDate;

    /**
     * 领取日期
     */
    @Column(length = 64)
    private Date receiveDate;

    @Column(length = 64)
    private BigDecimal amount;

    /**
     * 领取状态 1-未领取 2-已领取
     */
    private Integer status;

}
