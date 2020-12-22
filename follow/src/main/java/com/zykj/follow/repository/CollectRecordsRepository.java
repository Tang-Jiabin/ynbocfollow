package com.zykj.follow.repository;

import com.zykj.follow.common.PrizeType;
import com.zykj.follow.pojo.CollectRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tang
 */
@Repository
public interface CollectRecordsRepository extends JpaRepository<CollectRecords,String> {
    List<CollectRecords> findAllByTypeAndPhone(PrizeType wechatGold, String tokenPhone);

    List<CollectRecords> findAllByUserId(String userId);
}
