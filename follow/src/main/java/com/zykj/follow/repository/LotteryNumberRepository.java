package com.zykj.follow.repository;

import com.zykj.follow.pojo.LotteryNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author tang
 */
@Repository
public interface LotteryNumberRepository extends JpaRepository<LotteryNumber,String> {
    LotteryNumber findByUserId(String userId);
}
