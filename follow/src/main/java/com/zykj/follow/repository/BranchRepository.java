package com.zykj.follow.repository;

import com.zykj.follow.pojo.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Integer> {
    Branch findByBranchName(String name);
}
