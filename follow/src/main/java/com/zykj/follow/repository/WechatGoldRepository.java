package com.zykj.follow.repository;

import com.zykj.follow.pojo.WechatGold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WechatGoldRepository extends JpaRepository<WechatGold,Integer> {


    List<WechatGold> findAllBySurplusGreaterThanEqual(int i);
}
