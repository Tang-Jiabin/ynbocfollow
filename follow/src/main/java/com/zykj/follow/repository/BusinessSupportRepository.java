package com.zykj.follow.repository;

import com.zykj.follow.pojo.BusinessSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessSupportRepository extends JpaRepository<BusinessSupport,Integer> {
    List<BusinessSupport> findAllBySupportIdIn(List<Integer> staffSupportIdList);

    List<BusinessSupport> findAllByBranchId(Integer branchId);
}
