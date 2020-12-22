package com.zykj.follow.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 员工信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-16
 */

@Data
@Entity
@Table(name = "boc_yn_staff_info")
public class StaffInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer staffId;

    @Column(length = 64)
    private String jobNum;

    @Column(length = 64)
    private String name;

    @Column(length = 64)
    private String phone;

    @Column(length = 64)
    private String userId;

    private Date createDate;

    /**
     * 支行id
     */
    private Integer branchId;

    /**
     * 网点id
     */
    private Integer supportId;


}
