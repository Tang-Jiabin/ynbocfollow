package com.zykj.follow.repository;

import com.zykj.follow.pojo.PrizeInfo;
import com.zykj.follow.pojo.StaffPrizeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffPrizeInfoRepository extends JpaRepository<StaffPrizeInfo,Integer> {
    List<StaffPrizeInfo> findAllBySurplusGreaterThanEqual(int i);
}
