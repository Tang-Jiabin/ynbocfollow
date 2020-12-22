package com.zykj.follow.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * 营业厅
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-18
 */
@Data
@Entity
@Table(name = "boc_yn_business_support")
public class BusinessSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer supportId;

    private String supportName;

    private Integer branchId;


}
