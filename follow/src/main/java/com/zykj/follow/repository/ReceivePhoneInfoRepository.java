package com.zykj.follow.repository;

import com.zykj.follow.pojo.ReceivePhoneInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author tang
 */
@Repository
public interface ReceivePhoneInfoRepository extends JpaRepository<ReceivePhoneInfo,Integer> {
}
