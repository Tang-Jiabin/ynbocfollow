package com.zykj.follow.dto;

import lombok.Data;

/**
 * 效能
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-18
 */

@Data
public class EffectDTO {

    private String branch;
    private Integer activation;
    private Double activationEffect;
    private Integer pay;
    private Double payEffect;
    private Integer receive;
    private Integer receiveEffect;
    private Integer supportNum;
}
