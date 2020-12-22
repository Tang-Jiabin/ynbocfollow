package com.zykj.follow.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zykj.follow.common.PrizeType;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 奖品信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */

@Data
@Entity
@Table(name = "boc_yn_prize_info")
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
public class PrizeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length = 64)
    private Integer prizeId;

    @Column(length = 64)
    private String prizeName;

    @Column(length = 64)
    private String prizeImg;

    /**
     * 概率
     */
    private Double probability;

    /**
     * 总数
     */
    private Integer total;

    /**
     * 剩余数量
     */
    private Integer surplus;

    /**
     * 类型 0-立减金  1-谢谢参与  2-话费  3-手机
     */

    private PrizeType type;

    /**
     * 金额
     */
    @Column(length = 64)
    private BigDecimal amount;
}
