package com.zykj.follow.service;

import com.zykj.follow.pojo.PrizeInfo;
import com.zykj.follow.pojo.StaffPrizeInfo;
import com.zykj.follow.repository.StaffPrizeInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 员工抽奖信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-23
 */
@Service
public class StaffPrizeInfoService {

    private final StaffPrizeInfoRepository staffPrizeInfoRepository;

    @Autowired
    public StaffPrizeInfoService(StaffPrizeInfoRepository staffPrizeInfoRepository) {
        this.staffPrizeInfoRepository = staffPrizeInfoRepository;
    }

    public List<StaffPrizeInfo> getSurplusInfo() {
        return staffPrizeInfoRepository.findAllBySurplusGreaterThanEqual(1);
    }

    public StaffPrizeInfo save(StaffPrizeInfo prizeInfo) {
        return staffPrizeInfoRepository.save(prizeInfo);
    }
}
