package com.zykj.follow.repository;

import com.zykj.follow.pojo.PrizeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizeInfoRepository extends JpaRepository<PrizeInfo,Integer> {
    List<PrizeInfo> findAllBySurplusGreaterThanEqual(int i);
}
