package com.zykj.follow.service;

import com.zykj.follow.common.PrizeType;
import com.zykj.follow.pojo.CollectRecords;
import com.zykj.follow.repository.CollectRecordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 领取记录
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */
@Service
public class CollectRecordsService {

    @Autowired
    private CollectRecordsRepository collectRecordsRepository;

    public List<CollectRecords> findAllByTypeAndPhone(PrizeType wechatGold, String tokenPhone) {

        return collectRecordsRepository.findAllByTypeAndPhone(wechatGold, tokenPhone);
    }

    public CollectRecords save(CollectRecords collectRecords) {

        return collectRecordsRepository.save(collectRecords);
    }

    public List<CollectRecords> findAllByUserId(String userId) {
        return collectRecordsRepository.findAllByUserId(userId);
    }

    public CollectRecords findByRecordsId(String recordsId) {

        return collectRecordsRepository.getOne(recordsId);
    }
}
