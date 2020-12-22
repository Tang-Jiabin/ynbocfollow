package com.zykj.follow.repository;

import com.zykj.follow.pojo.PayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayOrderRepository extends JpaRepository<PayOrder,Integer> {
    PayOrder findByOrderNo(String orderNo);
}
