package com.zykj.follow.repository;

import com.zykj.follow.pojo.StaffInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffInfoRepository extends JpaRepository<StaffInfo,Integer> {
    StaffInfo findByUserId(String userId);

    List<StaffInfo> findAllByUserIdNotNull();

    StaffInfo findByJobNum(String jobNum);

    List<StaffInfo> findAllByBranchIdIn(List<Integer> branchIdList);

    List<StaffInfo> findAllBySupportIdIn(List<Integer> supportIdList);

    List<StaffInfo> findAllByBranchIdInAndUserIdNotNull(List<Integer> branchIdList);

}
