package com.zykj.follow.repository;

import com.zykj.follow.pojo.WeChatUserQrInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeChatUserQrInfoRepository extends JpaRepository<WeChatUserQrInfo,String> {
}
