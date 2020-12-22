package com.zykj.follow.dto;

import lombok.Data;

import javax.persistence.Column;

/**
 * 员工排序
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-16
 */
@Data
public class StaffRankingDto {

    private String nickname;

    private String headImgUrl;

    private Integer count;

    private Integer isMe;

    private String branch;

}
