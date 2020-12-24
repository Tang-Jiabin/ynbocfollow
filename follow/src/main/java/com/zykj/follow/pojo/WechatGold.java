package com.zykj.follow.pojo;

import com.zykj.follow.common.PrizeType;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 立减金
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-24
 */
@Data
@Entity
@Table(name = "boc_yn_wechat_gold")
public class WechatGold {

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
     * 金额类型
     */

    private int type;

    /**
     * 金额
     */
    @Column(length = 64)
    private BigDecimal amount;
}
