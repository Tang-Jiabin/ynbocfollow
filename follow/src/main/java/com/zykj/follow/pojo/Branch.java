package com.zykj.follow.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * 支行信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-18
 */
@Data
@Entity
@Table(name = "boc_yn_branch")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer branchId;

    private String branchName;
}
