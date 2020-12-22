package com.zykj.follow.controller;

import com.alibaba.fastjson.JSONObject;
import com.zykj.follow.common.Module;
import com.zykj.follow.common.annotation.Authorization;
import com.zykj.follow.common.http.ServerResponse;
import com.zykj.follow.common.interceptor.TokenManager;
import com.zykj.follow.dto.StaffRankingDto;
import com.zykj.follow.pojo.*;
import com.zykj.follow.repository.BusinessSupportRepository;
import com.zykj.follow.service.*;
import com.zykj.follow.utils.BocWebPayUtil;
import com.zykj.follow.utils.PhoneUtil;
import com.zykj.follow.utils.RedisUtil;
import com.zykj.follow.utils.SnowflakeIdFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 员工
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-15
 */
@Slf4j
@RestController
@RequestMapping(value = "staff", produces = MediaType.APPLICATION_JSON_VALUE)
public class StaffController {

    @Resource
    private HttpServletResponse response;
    @Resource
    private HttpServletRequest request;

    private final TokenManager tokenManager;
    private final UserService userService;
    private final RedisUtil redisUtil;
    private final StaffService staffService;
    private final WeChatUserQrInfoService qrInfoService;
    private final InvitationRecordService recordService;
    private final PayOrderService payOrderService;
    private final BusinessSupportRepository supportRepository;


    @Autowired
    public StaffController(TokenManager tokenManager, UserService userService, RedisUtil redisUtil, StaffService staffService, WeChatUserQrInfoService qrInfoService, InvitationRecordService recordService, PayOrderService payOrderService, BusinessSupportRepository supportRepository) {
        this.tokenManager = tokenManager;
        this.userService = userService;
        this.redisUtil = redisUtil;
        this.staffService = staffService;
        this.qrInfoService = qrInfoService;
        this.recordService = recordService;
        this.payOrderService = payOrderService;
        this.supportRepository = supportRepository;
    }

    /**
     * 微信回调
     *
     * @param code  回调代码
     * @param state 自定义字段
     */
    @RequestMapping(value = "callback", method = {RequestMethod.GET, RequestMethod.POST})
    public String callback(@RequestParam("code") String code, @RequestParam("state") String state) {

        JSONObject wxUserInfo = Module.getWeChatUserInfoJson(code, state);

        WechatUserInfo userInfo = userService.save(wxUserInfo);
        if (userInfo == null) {
            log.error("获取openid失败");
            return "";
        }

        //查找绑定手机
        String phone = userInfo.getStaffMobile();
        if (StringUtils.hasLength(phone)) {
            //生成token 跳转活动页
            String token = null;
            try {
                token = tokenManager.createToken(phone).getToken();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response.sendRedirect("/ynbocfollow/yuangong/home.html?token=" + token);
                return "";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //未绑定手机号 跳转绑定
        try {
            response.sendRedirect("/ynbocfollow/yuangong/bangding.html?sid=" + userInfo.getSid());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 绑定手机号
     *
     * @param phone 手机号
     * @param code  验证码
     * @param sid   sid
     * @return token
     */
    @GetMapping(value = "bindMobile")
    public ServerResponse<String> bindMobile(String name, String jobNum, String phone, String code, String sid) {

        if (!PhoneUtil.phoneValidation(phone)) {
            return ServerResponse.createMessage(400, "手机号格式错误");
        }

        String reCode = redisUtil.getString(phone);
        if (reCode == null) {
            return ServerResponse.createMessage(412, "验证码错误");
        }
        if (reCode.equals(code)) {
            if (!StringUtils.hasLength(sid)) {
                return ServerResponse.createMessage(411, "请稍后再试");
            }

            WechatUserInfo userInfo = userService.findBySid(sid);
            if (userInfo == null) {
                return ServerResponse.createMessage(412, "请关闭页面重新微信授权");
            }
            WechatUserInfo userPhone = userService.findByStaffMobile(phone);
            if (userPhone != null) {
                return ServerResponse.createMessage(413, "当前手机号已绑定其他账号");
            }
            userInfo.setStaffMobile(phone);
            userInfo = userService.save(userInfo);

            log.info("工号{} 姓名{} 手机号{} 微信ID{}", jobNum, name, phone, userInfo.getUserId());
            staffService.add(jobNum, name, phone, userInfo.getUserId(), new Date());

            String token = null;
            try {
                token = tokenManager.createToken(userInfo.getStaffMobile()).getToken();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return ServerResponse.createMessage(ServerResponse.OK, "成功", token);
        }

        return ServerResponse.createMessage(412, "验证码错误");
    }

    /**
     * 获取员工信息
     *
     * @param tokenPhone 手机号
     * @return 员工信息
     */
    @Authorization
    @GetMapping(value = "getStaffInfo")
    public ServerResponse<JSONObject> getStaffInfo(@RequestAttribute String tokenPhone) {

        WechatUserInfo userInfo = userService.findByStaffMobile(tokenPhone);
        if (userInfo == null) {
            return ServerResponse.createMessage(401, "请重新登录");
        }

        StaffInfo staffInfo = staffService.findByUserId(userInfo.getUserId());
        String paramQr = qrInfoService.getParamQr(userInfo.getUserId(), 2592000, "QR_STR_SCENE", 1002);

        Optional<BusinessSupport> supportOptional = supportRepository.findById(staffInfo.getSupportId());


        JSONObject staffInfoJson = new JSONObject();
        staffInfoJson.put("nickname", userInfo.getNickname());
        staffInfoJson.put("headImgUrl", userInfo.getHeadImgUrl());
        staffInfoJson.put("staffMobile", userInfo.getStaffMobile());

        if (supportOptional.isPresent()) {
            staffInfoJson.put("branch", supportOptional.get().getSupportName());
        }

        staffInfoJson.put("paramQr", paramQr);

        return ServerResponse.createMessage(ServerResponse.OK, "成功", staffInfoJson);

    }

    /**
     * 邀请详情
     *
     * @param tokenPhone 手机号
     * @return 邀请详细数据
     */
    @Authorization
    @GetMapping(value = "getInviteDetailed")
    public ServerResponse<JSONObject> getInviteDetailed(@RequestAttribute String tokenPhone) {

        WechatUserInfo userInfo = userService.findByStaffMobile(tokenPhone);

        List<InvitationRecord> recordList = recordService.findAllByInviteUserId(userInfo.getUserId());

        List<String> acceptUserIdList = recordList.stream().map(InvitationRecord::getAcceptUserId).collect(Collectors.toList());

        List<WechatUserInfo> acceptUserList = userService.findAllByUserIdIn(acceptUserIdList);

        JSONObject inviteDetailed = new JSONObject();
        List<JSONObject> jsonList = new ArrayList<>();
        JSONObject json;
        int follow = 0;
        int pay = 0;
        for (WechatUserInfo wechatUserInfo : acceptUserList) {
            Optional<InvitationRecord> recordOptional = recordList.stream().filter(record -> record.getAcceptUserId().equals(wechatUserInfo.getUserId())).findAny();
            if (recordOptional.isPresent()) {
                json = new JSONObject();
                if (recordOptional.get().getInviteStatus() == 1) {
                    json.put("status", "已关注");
                    follow = follow + 1;
                } else if (recordOptional.get().getInviteStatus() == 2) {
                    json.put("status", "已领取");
                    follow = follow + 1;
                } else if (recordOptional.get().getInviteStatus() == 3) {
                    json.put("status", "已支付");
                    pay = pay + 1;
                } else {
                    json.put("status", "关注");
                }
                json.put("nickname", wechatUserInfo.getNickname());
                json.put("headImgUrl", wechatUserInfo.getHeadImgUrl());
                json.put("subscribeTime", wechatUserInfo.getFirstSubscribeTime());
                jsonList.add(json);
            }
        }
        inviteDetailed.put("user", jsonList);
        inviteDetailed.put("pay", pay);
        inviteDetailed.put("follow", follow);

        return ServerResponse.createMessage(200, "成功", inviteDetailed);

    }

    /**
     * 排行
     *
     * @param tokenPhone 手机号
     * @return 排行
     */
    @Authorization
    @GetMapping(value = "getRanking")
    public ServerResponse getRanking(@RequestAttribute String tokenPhone) {

        List<StaffInfo> staffInfoList = staffService.findAllByUserIdNotNull();

        List<String> staffUserIdList = staffInfoList.stream().map(StaffInfo::getUserId).collect(Collectors.toList());
        List<Integer> staffSupportIdList = staffInfoList.stream().map(StaffInfo::getSupportId).collect(Collectors.toList());

        List<WechatUserInfo> staffUserList = userService.findAllByUserIdIn(staffUserIdList);

        List<InvitationRecord> recordList = recordService.findAllByInviteUserIdIn(staffUserIdList);

        List<BusinessSupport> supportList = supportRepository.findAllBySupportIdIn(staffSupportIdList);
        StaffRankingDto staffRankingDto;
        List<StaffRankingDto> staffList = new ArrayList<>();

        for (WechatUserInfo userInfo : staffUserList) {
            staffRankingDto = new StaffRankingDto();
            int count = 0;
            for (InvitationRecord record : recordList) {
                if (userInfo.getUserId().equals(record.getInviteUserId())) {
                    count = count + 1;
                }
            }
            for (StaffInfo staffInfo : staffInfoList) {
                if (staffInfo.getUserId().equals(userInfo.getUserId())) {
                    for (BusinessSupport support : supportList) {
                        if(support.getSupportId().equals(staffInfo.getSupportId())){
                            staffRankingDto.setBranch(support.getSupportName());
                        }
                    }
                    staffRankingDto.setNickname(staffInfo.getName());
                }
            }
            staffRankingDto.setCount(count);
            staffRankingDto.setHeadImgUrl(userInfo.getHeadImgUrl());
            staffRankingDto.setIsMe(0);

            if (tokenPhone.equals(userInfo.getStaffMobile())) {
                staffRankingDto.setIsMe(1);
            }
            staffList.add(staffRankingDto);
        }

        staffList = staffList.stream().sorted(Comparator.comparing(StaffRankingDto::getCount, Comparator.reverseOrder())).collect(Collectors.toList());

        return ServerResponse.createMessage(ServerResponse.OK, "成功", staffList);
    }

    /**
     * 支付参数
     *
     * @param code  微信回调code
     * @param state 自定义state
     * @return 支付信息
     */
    @GetMapping(value = "getPayParam")
    public ServerResponse<JSONObject> getPayParam(String code, String state) {

        log.info("code {}", code);
        log.info("state {}", state);

        JSONObject wxUserInfo = Module.getWeChatUserInfoJson(code, state);

        JSONObject json = JSONObject.parseObject(state);
        String staffPhone = json.getString("page");

        log.info(staffPhone);
        WechatUserInfo inviteUser = userService.findByStaffMobile(staffPhone);

        if (!StringUtils.hasLength(staffPhone)) {
            return ServerResponse.createMessage(410, "请关闭网页重新授权");
        }


        WechatUserInfo userInfo = userService.save(wxUserInfo);
        if (userInfo == null) {
            log.error("获取openid失败");
            return ServerResponse.createMessage(410, "请关闭网页重新授权");
        }

        JSONObject payParam = null;
        try {
            payParam = BocWebPayUtil.createPayParam(BocWebPayUtil.ORDER_NOTE, BocWebPayUtil.ORDER_AMOUNT, BocWebPayUtil.ORDER_URL);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return ServerResponse.createMessage(400, "系统繁忙，请稍后再试");
        }

        //创建订单
        PayOrder payOrder = new PayOrder();
        payOrder.setAcceptUserId(userInfo.getUserId());
        payOrder.setInviteUserId(inviteUser.getUserId());
        payOrder.setOrderNo(payParam.getString("orderNo"));
        payOrderService.save(payOrder);

        return ServerResponse.createMessage(200, "成功", payParam);
    }

    /**
     * 支付回调
     */
    @RequestMapping(value = "payCallback")
    public String payCallback() {
        String merchantNo = request.getParameter("merchantNo");
        String orderNo = request.getParameter("orderNo");
        String orderSeq = request.getParameter("orderSeq");
        String cardTyp = request.getParameter("cardTyp");
        String payTime = request.getParameter("payTime");
        String orderStatus = request.getParameter("orderStatus");
        String payAmount = request.getParameter("payAmount");
        String ibknum = request.getParameter("ibknum");
        String phoneNum = request.getParameter("phoneNum");
        String signData = request.getParameter("signData");
        String acctNo = request.getParameter("acctNo");
        String holderName = request.getParameter("holderName");

        log.info("merchantNo:{}  orderNo:{}  orderSeq:{}  cardTyp:{}  payTime:{}  orderStatus:{}  payAmount:{}  ibknum:{}  phoneNum:{}  acctNo:{}  holderName:{} ", merchantNo, orderNo, orderSeq, cardTyp, payTime, orderStatus, payAmount, ibknum, phoneNum, acctNo, holderName);

        PayOrder payOrder = payOrderService.findByOrderNo(orderNo);

        if (payOrder == null) {
            log.error("订单查询失败");
            return "";
        }
        payOrder.setMerchantNo(merchantNo);
        payOrder.setOrderSeq(orderSeq);
        payOrder.setCardTyp(cardTyp);
        payOrder.setPayTime(payTime);
        payOrder.setOrderStatus(orderStatus);
        payOrder.setPayAmount(payAmount);
        payOrder.setIbknum(ibknum);
        payOrder.setPhoneNum(phoneNum);
        payOrder.setAcctNo(acctNo);
        payOrder.setHolderName(holderName);
        payOrderService.save(payOrder);

        SnowflakeIdFactory factory = new SnowflakeIdFactory(SnowflakeIdFactory.getWorkerId(), 1L);
        String recordId = String.valueOf(factory.nextId());
        recordService.add(recordId, new Date(), payOrder.getInviteUserId(), payOrder.getAcceptUserId(), "PAY", 3);
        return "";
    }

}
