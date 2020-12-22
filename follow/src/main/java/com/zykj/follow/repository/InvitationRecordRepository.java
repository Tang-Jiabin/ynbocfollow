package com.zykj.follow.repository;

import com.zykj.follow.pojo.InvitationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface InvitationRecordRepository extends JpaRepository<InvitationRecord,String> {
    List<InvitationRecord> findAllByInviteUserId(String userId);

    InvitationRecord findByAcceptUserId(String acceptUserId);

    List<InvitationRecord> findAllByInviteUserIdIn(List<String> staffUserIdList);

    InvitationRecord findByInviteUserIdAndAcceptUserId(String inviteUserId, String acceptUserId);

    List<InvitationRecord> findAllByInviteUserIdInAndAcceptDateIsBetween(List<String> staffUserIdList, Date startDate, Date endDate);

}
