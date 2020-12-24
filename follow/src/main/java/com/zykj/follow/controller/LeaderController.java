package com.zykj.follow.controller;

import com.zykj.follow.common.http.ServerResponse;
import com.zykj.follow.dto.BranchDTO;
import com.zykj.follow.dto.EffectDTO;
import com.zykj.follow.pojo.Branch;
import com.zykj.follow.pojo.BusinessSupport;
import com.zykj.follow.pojo.InvitationRecord;
import com.zykj.follow.pojo.StaffInfo;
import com.zykj.follow.repository.BranchRepository;
import com.zykj.follow.repository.BusinessSupportRepository;
import com.zykj.follow.repository.InvitationRecordRepository;
import com.zykj.follow.repository.StaffInfoRepository;
import com.zykj.follow.utils.DateUtil;
import com.zykj.follow.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 领导
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-18
 */
@Slf4j
@RestController
@RequestMapping(value = "leader")
public class LeaderController {

    private final BranchRepository branchRepository;
    private final BusinessSupportRepository supportRepository;
    private final StaffInfoRepository staffInfoRepository;
    private final InvitationRecordRepository invitationRecordRepository;

    @Autowired
    public LeaderController(BranchRepository branchRepository, BusinessSupportRepository supportRepository, StaffInfoRepository staffInfoRepository, InvitationRecordRepository invitationRecordRepository) {
        this.branchRepository = branchRepository;
        this.supportRepository = supportRepository;
        this.staffInfoRepository = staffInfoRepository;
        this.invitationRecordRepository = invitationRecordRepository;
    }


    /**
     * 领导端首页数据
     *
     * @param index 日期
     * @return 首页数据
     */
    @GetMapping(value = "getBranchInfo")
    public ServerResponse<List<EffectDTO>> getBranchInfo(@RequestParam Integer index) {
        //支行信息
        List<Branch> branchList = branchRepository.findAll();

        List<Integer> branchIdList = branchList.stream().map(Branch::getBranchId).collect(Collectors.toList());

        List<StaffInfo> staffList = staffInfoRepository.findAllByBranchIdIn(branchIdList);

        List<String> staffUserIdList = staffList.stream().map(StaffInfo::getUserId).collect(Collectors.toList());

        Date startDate = new Date();
        Date endDate = new Date();

        switch (index) {
            case 1:
                //总数
                startDate = DateUtil.getSpecifiedDate(DateUtil.getTodayZeroHours(), -365);
                break;
            case 2:
                //今天
                startDate = DateUtil.getTodayZeroHours();
                break;
            case 3:
                //昨天
                startDate = DateUtil.getSpecifiedDate(DateUtil.getTodayZeroHours(), -1);
                endDate = DateUtil.getTodayZeroHours();
                break;
            case 4:
                //近7天
                startDate = DateUtil.getSpecifiedDate(DateUtil.getTodayZeroHours(), -7);
                break;
            case 5:
                startDate = DateUtil.getSpecifiedDate(DateUtil.getTodayZeroHours(), -30);
                //近30天
                break;
            default:
                startDate = DateUtil.getSpecifiedDate(DateUtil.getTodayZeroHours(), -31);
                break;

        }


        List<InvitationRecord> invitationList = invitationRecordRepository.findAllByInviteUserIdInAndAcceptDateIsBetween(staffUserIdList, startDate, endDate);

        List<BusinessSupport> supportList = supportRepository.findAll();

        Map<Integer, Long> map = supportList.stream().collect(Collectors.groupingBy(BusinessSupport::getBranchId, Collectors.counting()));

        List<EffectDTO> effectList = new ArrayList<>();

        EffectDTO effect = new EffectDTO();

        for (Branch branch : branchList) {

            effect = new EffectDTO();
            effect.setBranch(branch.getBranchName());
            effect.setActivation(0);
            effect.setPay(0);
            effect.setReceive(0);
            int supportNum = map.get(branch.getBranchId()).intValue();
            effect.setSupportNum(supportNum);

            int activation = 0;
            int pay = 0;
            int receive = 0;

            for (StaffInfo staffInfo : staffList) {
                if (branch.getBranchId().equals(staffInfo.getBranchId())) {
                    for (InvitationRecord record : invitationList) {
                        if (record.getInviteUserId().equals(staffInfo.getUserId())) {

                            activation = activation + 1;

                            if (record.getInviteStatus() == 3) {
                                pay = pay + 1;
                            }
                            if (record.getInviteStatus() == 2) {
                                receive = receive + 1;
                            }
                        }
                    }
                }
            }

            Double activationEffect;
            Double payEffect;
            Integer receiveEffect;

            if (activation == 0 || supportNum == 0) {
                activationEffect = 0D;
            } else {
                activationEffect = Double.valueOf(activation) / supportNum;
            }

            if (activation == 0 || receive == 0) {
                receiveEffect = 0;
            } else {
                receiveEffect = (receive / activation) * 100;
            }

            if (pay == 0 || supportNum == 0) {
                payEffect = 0D;
            } else {
                payEffect = Double.valueOf(pay) / supportNum;
            }

            effect.setActivation(activation);
            effect.setPay(pay);
            effect.setReceive(receive);
            effect.setActivationEffect((double) Math.round(activationEffect * 100) / 100);
            effect.setPayEffect((double) Math.round(payEffect * 100) / 100);
            effect.setReceiveEffect(receiveEffect);
            effectList.add(effect);
        }

        effectList = effectList.stream().sorted(Comparator.comparing(EffectDTO::getActivation).reversed()).collect(Collectors.toList());

        return ServerResponse.createMessage(200, "成功", effectList);
    }


    /**
     * 今日之星
     *
     * @return 今日之星
     */
    @GetMapping(value = "getTodayStar")
    public ServerResponse<List<EffectDTO>> getTodayStar() {
        List<BusinessSupport> supportList = supportRepository.findAll();

        List<Integer> supportIdList = supportList.stream().map(BusinessSupport::getSupportId).collect(Collectors.toList());

        List<StaffInfo> staffList = staffInfoRepository.findAllBySupportIdIn(supportIdList);

        List<String> staffUserIdList = staffList.stream().map(StaffInfo::getUserId).collect(Collectors.toList());

        Date startDate = startDate = DateUtil.getTodayZeroHours();
        Date endDate = new Date();

        List<InvitationRecord> invitationList = invitationRecordRepository.findAllByInviteUserIdInAndAcceptDateIsBetween(staffUserIdList, startDate, endDate);

        List<EffectDTO> effectList = new ArrayList<>();

        EffectDTO effect = new EffectDTO();

        for (BusinessSupport support : supportList) {

            effect = new EffectDTO();
            effect.setBranch(support.getSupportName());
            effect.setActivation(0);
            effect.setPay(0);
            effect.setReceive(0);
            effect.setSupportNum(0);

            int activation = 0;

            for (StaffInfo staffInfo : staffList) {
                if (support.getSupportId().equals(staffInfo.getSupportId())) {
                    for (InvitationRecord record : invitationList) {
                        if (record.getInviteUserId().equals(staffInfo.getUserId())) {
                            activation = activation + 1;
                        }
                    }
                }
            }
            effect.setActivation(activation);
            effectList.add(effect);
        }

        return ServerResponse.createMessage(200, "成功", effectList);


    }

    /**
     * 使用数据
     *
     * @return 使用数据
     */
    @GetMapping(value = "getUtilization")
    public ServerResponse<List<BranchDTO>> getUtilization() {
        List<Branch> branchList = branchRepository.findAll();

        List<Integer> branchIdList = branchList.stream().map(Branch::getBranchId).collect(Collectors.toList());

        List<BusinessSupport> supportList = supportRepository.findAll();

        List<StaffInfo> staffInfoList = staffInfoRepository.findAllByBranchIdInAndUserIdNotNull(branchIdList);

        BranchDTO branchDTO;
        List<BranchDTO> branchDTOList = new ArrayList<>();

        for (Branch branch : branchList) {
            branchDTO = new BranchDTO();
            int supportNum = 0;
            int use = 0;
            for (BusinessSupport support : supportList) {
                if (branch.getBranchId().equals(support.getBranchId())) {
                    supportNum = supportNum + 1;
                    for (StaffInfo staffInfo : staffInfoList) {
                        if (support.getSupportId().equals(staffInfo.getSupportId())) {
                            use = use + 1;
                        }
                    }
                }
            }
            branchDTO.setBranchName(branch.getBranchName());
            branchDTO.setSupportNum(supportNum);
            branchDTO.setUse(use);
            branchDTO.setNotUse(supportNum - use);
            int utilization = 0;
            if (supportNum != 0 && use != 0) {
                utilization = use * 100 / supportNum;
            }
            branchDTO.setUtilization(utilization);
            branchDTOList.add(branchDTO);
        }

        return ServerResponse.createMessage(ServerResponse.OK, "成功", branchDTOList);
    }

    /**
     * 网点抽奖
     * @param name 网点名称
     * @return 详细数据
     */
    @GetMapping(value = "getBusinessInfo")
    public ServerResponse<List<EffectDTO>> getBusinessInfo(String name) {

        log.info("name {}", name);

        Branch branch = branchRepository.findByBranchName(name);

        if (branch == null) {
            return ServerResponse.createMessage(410, "网点不存在");
        }
        //网点信息
        List<BusinessSupport> supportList = supportRepository.findAllByBranchId(branch.getBranchId());

        List<Integer> supportIdList = supportList.stream().map(BusinessSupport::getSupportId).collect(Collectors.toList());

        List<StaffInfo> staffList = staffInfoRepository.findAllBySupportIdIn(supportIdList);

        List<String> staffUserIdList = staffList.stream().map(StaffInfo::getUserId).collect(Collectors.toList());

//        Date startDate = startDate = DateUtil.getSpecifiedDate(DateUtil.getTodayZeroHours(), -365);
        Date endDate = new Date();

        List<InvitationRecord> invitationList = invitationRecordRepository.findAllByInviteUserIdInAndAcceptDateIsBetween(staffUserIdList, DateUtil.getTodayZeroHours(), endDate);

        List<EffectDTO> effectList = new ArrayList<>();

        EffectDTO effect;

        for (BusinessSupport support : supportList) {

            effect = new EffectDTO();
            effect.setBranch(support.getSupportName());
            effect.setActivation(0);
            effect.setPay(0);
            effect.setReceive(0);
            effect.setSupportNum(0);

            int activation = 0;
            int pay = 0;
            int receive = 0;

            for (StaffInfo staffInfo : staffList) {
                if (support.getSupportId().equals(staffInfo.getSupportId())) {
                    for (InvitationRecord record : invitationList) {
                        if (record.getInviteUserId().equals(staffInfo.getUserId())) {
                            activation = activation + 1;
                            if (record.getInviteStatus() == 3) {
                                pay = pay + 1;
                            }
                            if (record.getInviteStatus() == 2) {
                                receive = receive + 1;
                            }
                        }
                    }
                }
            }


            Integer receiveEffect;


            if (activation == 0 || receive == 0) {
                receiveEffect = 0;
            } else {
                receiveEffect = (receive / activation) * 100;
            }


            effect.setActivation(activation);
            effect.setPay(pay);
            effect.setReceive(receive);
            effect.setReceiveEffect(receiveEffect);
            effectList.add(effect);

        }

        return ServerResponse.createMessage(ServerResponse.OK, "成功", effectList);
    }


    @GetMapping(value = "test")
    public void test() {
        List<Branch> branchList = branchRepository.findAll();
        List<BusinessSupport> supportList = supportRepository.findAll();
        List<StaffInfo> staffInfoList = staffInfoRepository.findAll();
        String path = "/Users/tang/Desktop/1.xlsx";
        String[] title = {"支行", "分行", "员工号", "姓名"};
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        for (Branch branch : branchList) {
            for (BusinessSupport support : supportList) {
                if (support.getBranchId().equals(branch.getBranchId())) {
                    for (StaffInfo staffInfo : staffInfoList) {
                        if (support.getSupportId().equals(staffInfo.getSupportId())) {
                            map = new HashMap<>();
                            map.put("支行", branch.getBranchName());
                            map.put("分行", support.getSupportName());
                            map.put("员工号", staffInfo.getJobNum());
                            map.put("姓名", staffInfo.getName());
                            list.add(map);
                        }
                    }
                }
            }
        }

        FileUtil.exportFile(title, list, path);

    }
}