package com.zykj.follow.service;

import com.zykj.follow.pojo.PayOrder;
import com.zykj.follow.repository.PayOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 支付订单
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-17
 */

@Service
public class PayOrderService {


    private final PayOrderRepository payOrderRepository;

    @Autowired
    public PayOrderService(PayOrderRepository payOrderRepository) {
        this.payOrderRepository = payOrderRepository;
    }

    public PayOrder save(PayOrder payOrder) {
        return payOrderRepository.save(payOrder);
    }

    public PayOrder findByOrderNo(String orderNo) {
        return payOrderRepository.findByOrderNo(orderNo);
    }
}
