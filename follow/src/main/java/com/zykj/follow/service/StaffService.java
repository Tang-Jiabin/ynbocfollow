package com.zykj.follow.service;

import com.zykj.follow.pojo.StaffInfo;
import com.zykj.follow.repository.StaffInfoRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 员工服务
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-16
 */
@Service
public class StaffService {

    private StaffInfoRepository staffInfoRepository;

    public StaffService(StaffInfoRepository staffInfoRepository) {
        this.staffInfoRepository = staffInfoRepository;
    }

    public StaffInfo add(String jobNum, String name, String phone, String userId, Date date) {

        StaffInfo staffInfo = staffInfoRepository.findByJobNum(jobNum);
        if (staffInfo == null) {
            staffInfo = new StaffInfo();
        }
        staffInfo.setJobNum(jobNum);
        staffInfo.setName(name);
        staffInfo.setPhone(phone);
        staffInfo.setUserId(userId);
        staffInfo.setCreateDate(date);

        return staffInfoRepository.save(staffInfo);
    }

    public List<StaffInfo> findAll() {
        return staffInfoRepository.findAll();
    }

    public StaffInfo findByUserId(String userId) {
        return staffInfoRepository.findByUserId(userId);
    }

    public List<StaffInfo> findAllByUserIdNotNull() {

        return staffInfoRepository.findAllByUserIdNotNull();
    }
}
