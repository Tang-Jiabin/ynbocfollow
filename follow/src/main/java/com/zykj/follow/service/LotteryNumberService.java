package com.zykj.follow.service;

import com.zykj.follow.pojo.LotteryNumber;
import com.zykj.follow.repository.LotteryNumberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 抽奖次数
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */

@Service
public class LotteryNumberService {

    @Autowired
    private LotteryNumberRepository lotteryNumberRepository;
    public LotteryNumber findByUserId(String userId) {
        return lotteryNumberRepository.findByUserId(userId);
    }

    public LotteryNumber save(LotteryNumber lotteryNumber) {
        return lotteryNumberRepository.save(lotteryNumber);
    }
}
