package com.zykj.follow.service;

import com.zykj.follow.pojo.PrizeInfo;
import com.zykj.follow.repository.PrizeInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 奖品信息
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */
@Service
public class PrizeInfoService {
    @Autowired
    private PrizeInfoRepository prizeInfoRepository;

    public List<PrizeInfo> getSurplusInfo() {
        return prizeInfoRepository.findAllBySurplusGreaterThanEqual(1);
    }

    public PrizeInfo save(PrizeInfo prizeInfo) {
        return prizeInfoRepository.save(prizeInfo);
    }
}
