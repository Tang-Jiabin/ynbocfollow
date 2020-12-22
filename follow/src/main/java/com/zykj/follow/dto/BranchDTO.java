package com.zykj.follow.dto;

import lombok.Data;

/**
 * 地区使用率
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-18
 */
@Data
public class BranchDTO {

    private String branchName;
    private Integer supportNum;
    private Integer use;
    private Integer notUse;
    private Integer utilization;


}
