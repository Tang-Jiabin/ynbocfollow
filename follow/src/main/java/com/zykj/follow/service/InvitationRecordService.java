package com.zykj.follow.service;

import com.zykj.follow.pojo.InvitationRecord;
import com.zykj.follow.repository.InvitationRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 邀请
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-15
 */
@Service
public class InvitationRecordService {

    private final InvitationRecordRepository invitationRecordRepository;

    public InvitationRecordService(InvitationRecordRepository invitationRecordRepository) {
        this.invitationRecordRepository = invitationRecordRepository;
    }

    public InvitationRecord add(String inviteId, Date acceptDate, String inviteUserId, String acceptUserId, String qrScene,Integer status) {

        InvitationRecord record = invitationRecordRepository.findByAcceptUserId(acceptUserId);
        if (record == null) {
            record = new InvitationRecord();
            record.setInviteId(inviteId);
            record.setAcceptDate(acceptDate);
            record.setInviteUserId(inviteUserId);
            record.setAcceptUserId(acceptUserId);
            record.setQrScene(qrScene);
            record.setInviteStatus(0);
        }
        if(status > record.getInviteStatus()){
            record.setInviteStatus(status);
        }

        return invitationRecordRepository.save(record);
    }

    public List<InvitationRecord> findAllByInviteUserId(String userId) {
        return invitationRecordRepository.findAllByInviteUserId(userId);
    }

    public List<InvitationRecord> findAllByInviteUserIdIn(List<String> staffUserIdList) {
        return invitationRecordRepository.findAllByInviteUserIdIn(staffUserIdList);
    }

    public InvitationRecord findByInviteUserIdAndAcceptUserId(String inviteUserId, String acceptUserId) {
        return invitationRecordRepository.findByInviteUserIdAndAcceptUserId(inviteUserId,acceptUserId);
    }
}
