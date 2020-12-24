package com.zykj.follow.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zykj.follow.common.AliasMethod;
import com.zykj.follow.common.Module;
import com.zykj.follow.common.PrizeType;
import com.zykj.follow.common.annotation.Authorization;
import com.zykj.follow.common.http.OkhttpUtil;
import com.zykj.follow.common.http.ServerResponse;
import com.zykj.follow.common.interceptor.TokenManager;
import com.zykj.follow.common.sms.CCPRestSDK;
import com.zykj.follow.common.sms.DateUtil;
import com.zykj.follow.pojo.*;
import com.zykj.follow.repository.ReceivePhoneInfoRepository;
import com.zykj.follow.repository.WechatGoldRepository;
import com.zykj.follow.service.*;
import com.zykj.follow.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-07
 */
@Slf4j
@RestController
@RequestMapping(value = "user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Resource
    private HttpServletResponse response;
    private final UserService userService;
    private final TokenManager tokenManager;
    private final RedisUtil redisUtil;
    private final CollectRecordsService collectRecordsService;
    private final LotteryNumberService lotteryNumberService;
    private final PrizeInfoService prizeInfoService;
    private final ReceivePhoneInfoRepository receivePhoneInfoRepository;
    private final WeChatUserQrInfoService qrInfoService;
    private final InvitationRecordService invitationRecordService;
    private final WechatGoldRepository wechatGoldRepository;

    @Autowired
    public UserController(InvitationRecordService invitationRecordService, ReceivePhoneInfoRepository receivePhoneInfoRepository, PrizeInfoService prizeInfoService, LotteryNumberService lotteryNumberService, CollectRecordsService collectRecordsService, RedisUtil redisUtil, TokenManager tokenManager, UserService userService, WeChatUserQrInfoService qrInfoService, WechatGoldRepository wechatGoldRepository) {
        this.userService = userService;
        this.tokenManager = tokenManager;
        this.redisUtil = redisUtil;
        this.collectRecordsService = collectRecordsService;
        this.lotteryNumberService = lotteryNumberService;
        this.prizeInfoService = prizeInfoService;
        this.receivePhoneInfoRepository = receivePhoneInfoRepository;
        this.invitationRecordService = invitationRecordService;
        this.qrInfoService = qrInfoService;
        this.wechatGoldRepository = wechatGoldRepository;
    }

    /**
     * 获取授权链接
     *
     * @param from 入口
     * @return 授权链接
     */
    @GetMapping(value = "/getAuthLink")
    public ServerResponse<String> getAuthLink(@RequestParam String from, @RequestParam(name = "staffPhone", required = false) String staffPhone) {
        JSONObject json = new JSONObject();
        String sid = "0";

        SnowflakeIdFactory factory = new SnowflakeIdFactory(1L, 1L);
        sid = String.valueOf(factory.nextId());
        json.put("sid", sid);
        json.put("page", 1);
        String url;
        if (Module.USER.equals(from)) {
            url = getOauthUrl(json, "user/callback");
        } else if (Module.STAFF.equals(from)) {
            url = getOauthUrl(json, "staff/callback");
        } else if (Module.PAY.equals(from)) {
            json.put("page", staffPhone);
            url = getOauthUrl(json, "yuangong/pay.html");
        } else {
            return ServerResponse.createMessage(411, "路径错误");
        }
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ServerResponse.createMessage(200, "成功", url);
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
        String phone = userInfo.getActivityMobile();
        log.info("phone{} ", phone);
        if (StringUtils.hasLength(phone)) {
            //生成token 跳转活动页
            String token = null;
            try {
                token = tokenManager.createToken(phone).getToken();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response.sendRedirect("/ynbocfollow/yonghu/home.html?token=" + token);
                return "";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //未绑定手机号 跳转绑定
        try {
            response.sendRedirect("/ynbocfollow/yonghu/bangding.html?sid=" + userInfo.getSid());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 事件推送
     *
     * @param body 推送数据
     */
    @RequestMapping(value = "notification", method = {RequestMethod.GET, RequestMethod.POST})
    public void notification(@RequestBody String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        String aesUserInfo = jsonObject.getString("userInfo");

        String userInfoStr = null;
        try {
            userInfoStr = AESUtil.Decrypt(aesUserInfo, AESUtil.AES_SKEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!StringUtils.hasLength(userInfoStr)) {
            return;
        }

        log.info("推送事件" + userInfoStr);
        JSONObject userJosn = JSONObject.parseObject(userInfoStr);

        WechatUserInfo userInfo = userService.save(userJosn);

        //领取成功 设置邀请状态 1-已关注
        addInvitationRecord(userInfo, 1);

    }

    /**
     * 获取验证码
     *
     * @param phone 手机号
     * @return 验证码发送状态
     */
    @GetMapping(value = "getCaptcha")
    public ServerResponse<String> getCaptcha(@RequestParam String phone) {
        log.info("手机号：" + phone);

        //手机号格式验证
        if (!PhoneUtil.phoneValidation(phone)) {
            return ServerResponse.createMessage(400, "手机号格式错误");
        }

        //生成6位数验证码
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        //使用发短信SDK
        CCPRestSDK ccpRestSDK = new CCPRestSDK();
        Map<String, Object> result = ccpRestSDK.sendTemplateSMS(phone, "443273", new String[]{code, "10分钟"});
        log.info("短信发送结果：" + result);
        //如果返回成功则存入redis
        if ("000000".equals(result.get("statusCode"))) {
            redisUtil.setStringSECONDS(phone, code, 60 * 10L);
        }
        log.info("验证码：" + code);
        redisUtil.setStringSECONDS(phone, code, 60 * 10L);

        return ServerResponse.createMessage(200, "短信发送成功");
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
    public ServerResponse<String> bindMobile(String phone, String code, String sid) {

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
            WechatUserInfo userPhone = userService.findByActivityMobile(phone);
            if (userPhone != null) {
                return ServerResponse.createMessage(413, "当前手机号已绑定其他账号");
            }
            userInfo.setActivityMobile(phone);
            userInfo = userService.save(userInfo);

            String token = null;
            try {
                token = tokenManager.createToken(userInfo.getActivityMobile()).getToken();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return ServerResponse.createMessage(ServerResponse.OK, "成功", token);
        }

        return ServerResponse.createMessage(412, "验证码错误");
    }

    /**
     * 领取立减金
     *
     * @param tokenPhone 手机号
     * @return 领取状态
     */
    @Authorization
    @GetMapping(value = "getWechatGold")
    public synchronized ServerResponse<JSONObject> getWechatGold(@RequestAttribute String tokenPhone) {
        WechatUserInfo userInfo = userService.findByActivityMobile(tokenPhone);
        if (userInfo == null) {
            return ServerResponse.createMessage(411, "请绑定手机号后重试");
        }

        //验证领取记录
        List<CollectRecords> collectRecordsList = collectRecordsService.findAllByTypeAndPhone(PrizeType.WECHAT_GOLD, tokenPhone);
        if (collectRecordsList.size() > 0) {
            return ServerResponse.createMessage(412, "不能重复领取");
        }

        //验证关注时间
        LocalDate localDate = LocalDate.of(2020, 12, 1);
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        Date date = Date.from(zonedDateTime.toInstant());
        Date subDate = userInfo.getFirstSubscribeTime();
        log.info("活动日期 " + DateUtil.dateToStr(date, 12));
        log.info("关注日期 " + DateUtil.dateToStr(subDate, 12));
        int difference = DateUtil.difference(date, subDate);
        if (difference >= 0) {
            return ServerResponse.createMessage(411, "仅限新关注用户参加活动");
        }

        //领取金额
        List<WechatGold> wechatGoldList = wechatGoldRepository.findAllBySurplusGreaterThanEqual(1);

        WechatGold wechatGold;
        if (wechatGoldList.size() >= 1) {
            wechatGold = getWechatGoldInfo(wechatGoldList);
        } else {
            return ServerResponse.createMessage(410, "当前奖品已派发完毕，请稍后重新参与");
        }
        if (wechatGold == null) {
            return ServerResponse.createMessage(410, "当前奖品已派发完毕，请稍后重新参与");
        }
        BigDecimal amount = wechatGold.getAmount();

        //调用领取接口
        String ljjUrl = getLjjUrl(wechatGold.getType());
        if (!StringUtils.hasLength(ljjUrl)) {
            return ServerResponse.createMessage(414, "领取失败");
        }
        wechatGold.setSurplus(wechatGold.getSurplus() - 1);
        wechatGoldRepository.save(wechatGold);

        //创建领取记录
        SnowflakeIdFactory factory = new SnowflakeIdFactory(1L, 1L);
        CollectRecords collectRecords = createCollectRecords(tokenPhone, userInfo, amount.doubleValue(), factory);

        //领取成功 设置邀请状态 2-已领取立减金
        addInvitationRecord(userInfo, 2);

        JSONObject json = new JSONObject();
        json.put("prizeName", "微信立减金");
        json.put("amount", collectRecords.getAmount());
        json.put("ljjUrl", ljjUrl);
        return ServerResponse.createMessage(ServerResponse.OK, "成功", json);
    }


    /**
     * 获取可抽奖次数
     *
     * @param tokenPhone 手机号
     * @return 可抽奖次数
     */
    @Authorization
    @GetMapping(value = "getLotteryNumber")
    public ServerResponse<Object> getLotteryNumber(@RequestAttribute String tokenPhone) {

        log.info("获取抽奖次数" + tokenPhone);
        WechatUserInfo userInfo = userService.findByActivityMobile(tokenPhone);
        if (userInfo == null) {
            return ServerResponse.createMessage(411, "请绑定手机号后重试");
        }

        LotteryNumber lotteryNumber = lotteryNumberService.findByUserId(userInfo.getUserId());

        if (lotteryNumber == null) {
            lotteryNumber = new LotteryNumber();
            SnowflakeIdFactory factory = new SnowflakeIdFactory(1L, 1L);
            String lotteryId = String.valueOf(factory.nextId());
            lotteryNumber.setLotteryId(lotteryId);
            lotteryNumber.setUserId(userInfo.getUserId());
            lotteryNumber.setPhone(tokenPhone);
            lotteryNumber.setTotalSurplusNumber(0);
            lotteryNumber.setInvitationTotal(0);
            lotteryNumber.setSurplusNumber(0);
            lotteryNumber.setInvitationSurplus(0);
            lotteryNumber = lotteryNumberService.save(lotteryNumber);
        }

        List<InvitationRecord> recordList = invitationRecordService.findAllByInviteUserId(userInfo.getUserId());
        List<String> acceptIdList = recordList.stream().map(InvitationRecord::getAcceptUserId).collect(Collectors.toList());

        List<WechatUserInfo> acceptUserList = userService.findAllByUserIdIn(acceptIdList);

        List<JSONObject> jsonList = new ArrayList<>();
        JSONObject inviteJson = new JSONObject();

        for (InvitationRecord record : recordList) {
            for (WechatUserInfo wechatUserInfo : acceptUserList) {
                if (record.getAcceptUserId().equals(wechatUserInfo.getUserId())) {
                    inviteJson = new JSONObject();
                    inviteJson.put("acceptDate", record.getAcceptDate());
                    inviteJson.put("nickname", wechatUserInfo.getNickname());
                    inviteJson.put("status", record.getInviteStatus() == 2 ? "已领取" : "已关注");
                    jsonList.add(inviteJson);
                }
            }
        }

        JSONObject json = new JSONObject();
        json.put("surplusNumber", lotteryNumber.getSurplusNumber());
        json.put("invitationTotal", lotteryNumber.getInvitationTotal());
        json.put("nickname", userInfo.getNickname());
        json.put("headImgUrl", userInfo.getHeadImgUrl());
        json.put("activityMobile", userInfo.getActivityMobile());
        json.put("inviteList", jsonList);

        return ServerResponse.createMessage(ServerResponse.OK, "成功", json);
    }

    /**
     * 抽奖
     *
     * @param tokenPhone token
     * @return 抽奖结果
     */
    @Authorization
    @GetMapping(value = "luckDraw")
    public synchronized ServerResponse<JSONObject> luckDraw(@RequestAttribute String tokenPhone) {

        WechatUserInfo userInfo = userService.findByActivityMobile(tokenPhone);
        if (userInfo == null) {
            return ServerResponse.createMessage(411, "请绑定手机号后重试");
        }

        //验证抽奖次数
        LotteryNumber lotteryNumber = lotteryNumberService.findByUserId(userInfo.getUserId());
        if (lotteryNumber == null) {
            return ServerResponse.createMessage(412, "请稍后再试");
        }
        if (lotteryNumber.getSurplusNumber() <= 0) {
            return ServerResponse.createMessage(413, "抽奖次数不足！");
        }


        //获取奖品列表
        List<PrizeInfo> prizeInfoList = prizeInfoService.getSurplusInfo();

        //抽取奖品
        PrizeInfo prizeInfo;
        if (prizeInfoList.size() >= 1) {
            prizeInfo = getPrizeInfo(prizeInfoList);
        } else {
            return ServerResponse.createMessage(410, "当前奖品已派发完毕，请稍后重新参与");
        }
        if (prizeInfo == null) {
            return ServerResponse.createMessage(410, "当前奖品已派发完毕，请稍后重新参与");
        }

        JSONObject resultData = new JSONObject();

        //type 类型 1-立减金  2-谢谢参与  3-话费  4-手机
        PrizeType prizeType = prizeInfo.getType();

        if (prizeType != PrizeType.THANKS) {
            //写入数据库
            SnowflakeIdFactory factory = new SnowflakeIdFactory(1L, 1L);
            String orderId = String.valueOf(factory.nextId());
            CollectRecords collectRecords = new CollectRecords();
            collectRecords.setRecordsId(orderId);
            collectRecords.setUserId(userInfo.getUserId());
            collectRecords.setPhone(tokenPhone);
            collectRecords.setPrizeName(prizeInfo.getPrizeName());
            collectRecords.setPrizeImg(prizeInfo.getPrizeImg());
            collectRecords.setType(prizeInfo.getType());
            collectRecords.setRecordsDate(new Date());
            collectRecords.setAmount(prizeInfo.getAmount());
            collectRecords.setStatus(1);
            collectRecordsService.save(collectRecords);
        }
        resultData.put("prizeName", prizeInfo.getPrizeName());
        resultData.put("prizeImg", prizeInfo.getPrizeImg());

        //减去抽奖次数
        prizeInfo.setSurplus(prizeInfo.getSurplus() - 1);
        prizeInfo = prizeInfoService.save(prizeInfo);
        lotteryNumber.setSurplusNumber(lotteryNumber.getSurplusNumber() - 1);
        lotteryNumberService.save(lotteryNumber);

        return ServerResponse.createMessage(ServerResponse.OK, "成功", resultData);
    }

    /**
     * 获取奖品列表
     *
     * @param tokenPhone 手机号
     * @return 奖品列表
     */
    @Authorization
    @GetMapping(value = "getPrizeList")
    public ServerResponse<JSONObject> getPrizeList(@RequestAttribute String tokenPhone) {
        WechatUserInfo userInfo = userService.findByActivityMobile(tokenPhone);

        List<CollectRecords> collectRecordsList = collectRecordsService.findAllByUserId(userInfo.getUserId());
        LotteryNumber lotteryNumber = lotteryNumberService.findByUserId(userInfo.getUserId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", collectRecordsList.size());
        jsonObject.put("collectRecordsList", collectRecordsList);
        jsonObject.put("surplusNumber", lotteryNumber.getSurplusNumber());

        return ServerResponse.createMessage(200, "成功", jsonObject);


    }

    /**
     * 查看奖品信息
     *
     * @param recordsId  奖品id
     * @param tokenPhone 手机号
     * @return 奖品详情
     */
    @Authorization
    @GetMapping(value = "getPrizeInfo")
    public ServerResponse<JSONObject> getPrizeInfo(@RequestParam String recordsId, @RequestAttribute String tokenPhone) {

        CollectRecords collectRecords = collectRecordsService.findByRecordsId(recordsId);
        if (collectRecords == null) {
            return ServerResponse.createMessage(411, "未查询到奖品信息", null);
        }
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(collectRecords);

        return ServerResponse.createMessage(200, "成功", jsonObject);
    }

    /**
     * 领奖
     *
     * @param tokenPhone 登录信息
     * @param recordsId  奖品id
     * @param phone      领奖手机号
     * @param address    地址
     * @param name       名字
     * @return 领取状态
     */
    @Authorization
    @GetMapping(value = "getUserInfo")
    public synchronized ServerResponse<String> getUserInfo(@RequestAttribute String tokenPhone, @RequestParam String recordsId, @RequestParam String phone, @RequestParam String address, @RequestParam String name) {

        log.info("token{} 手机号{} 姓名{} 地址{}", tokenPhone, phone, name, address);

        WechatUserInfo userInfo = userService.findByActivityMobile(tokenPhone);
        if (userInfo == null) {
            return ServerResponse.createMessage(411, "用户不存在");
        }

        CollectRecords collectRecords = collectRecordsService.findByRecordsId(recordsId);

        if (collectRecords == null) {
            return ServerResponse.createMessage(412, "用户不存在");
        }

        if (collectRecords.getStatus() != 1) {
            return ServerResponse.createMessage(414, "不能重复领取");
        }

        if (!StringUtils.hasLength(phone)) {
            return ServerResponse.createMessage(413, "请输入手机号");
        }

        if (collectRecords.getType() == PrizeType.PHONE_BILL) {
            collectRecords.setStatus(2);
            collectRecords.setReceiveDate(new Date());
            collectRecordsService.save(collectRecords);
            String money = collectRecords.getAmount().toString();
            SnowflakeIdFactory factory = new SnowflakeIdFactory(SnowflakeIdFactory.getWorkerId(), 1L);
            String orderId = String.valueOf(factory.nextId());
            int state = HFUtils.submitOrder(phone, orderId, money, HFUtils.hfUrl, HFUtils.szAgentId, HFUtils.key);
            log.info("充值话费 手机号{} 金额{} 状态{}", phone, money, state);
        } else if (collectRecords.getType() == PrizeType.PHONE) {

            if (!StringUtils.hasLength(name) || !StringUtils.hasLength(phone) || !StringUtils.hasLength(address)) {
                return ServerResponse.createMessage(415, "请填写领取信息");
            }


            collectRecords.setStatus(2);
            collectRecords.setReceiveDate(new Date());
            collectRecordsService.save(collectRecords);

            ReceivePhoneInfo phoneInfo = new ReceivePhoneInfo();
            phoneInfo.setName(name);
            phoneInfo.setPhone(phone);
            phoneInfo.setAddress(address);
            phoneInfo.setCreateDate(new Date());
            phoneInfo = receivePhoneInfoRepository.save(phoneInfo);

            log.info("领取手机 姓名{} 手机号{} 地址{}", name, phone, address);

        } else {
            return ServerResponse.createMessage(412, "用户不存在");
        }


        return ServerResponse.createMessage(ServerResponse.OK, "成功");
    }

    /**
     * 获取带参二维码
     *
     * @param tokenPhone 登录信息
     * @return 二维码
     */
    @Authorization
    @CrossOrigin
    @GetMapping(value = "getParamQr")
    public ServerResponse<String> getParamQr(@RequestAttribute String tokenPhone) {

        WechatUserInfo userInfo = userService.findByActivityMobile(tokenPhone);

        if (userInfo == null) {
            return ServerResponse.createMessage(401, "请重新登录");
        }

        String paramQr = qrInfoService.getParamQr(userInfo.getUserId(), 2592000, "QR_STR_SCENE", 1001);

        return ServerResponse.createMessage(ServerResponse.OK, "成功", paramQr);
    }

    /**
     * 按照几率抽奖
     *
     * @param prizeInfoList 奖品信息集合
     * @return 奖品
     */
    private PrizeInfo getPrizeInfo(List<PrizeInfo> prizeInfoList) {
        TreeMap<Integer, Double> map = new TreeMap<Integer, Double>();
        prizeInfoList.forEach(prizeInfo -> map.put(prizeInfo.getPrizeId(), prizeInfo.getProbability()));

        List<Double> list = new ArrayList<Double>(map.values());
        List<Integer> gifts = new ArrayList<Integer>(map.keySet());

        AliasMethod method = new AliasMethod(list);
        int index = method.next();
        Integer keyNum = gifts.get(index);

        //测试
        Optional<PrizeInfo> cartOptional = prizeInfoList.stream().filter(item -> item.getPrizeId().equals(keyNum)).findFirst();
        if (cartOptional.isPresent()) {
            return cartOptional.get();
        } else {
            log.info("奖品id错误：id" + keyNum);
            return null;
        }


    }

    /**
     * 按照几率抽奖
     *
     * @param wechatGoldInfoList 奖品信息集合
     * @return 奖品
     */
    private WechatGold getWechatGoldInfo(List<WechatGold> wechatGoldInfoList) {
        TreeMap<Integer, Double> map = new TreeMap<Integer, Double>();
        wechatGoldInfoList.forEach(prizeInfo -> map.put(prizeInfo.getPrizeId(), prizeInfo.getProbability()));

        List<Double> list = new ArrayList<Double>(map.values());
        List<Integer> gifts = new ArrayList<Integer>(map.keySet());

        AliasMethod method = new AliasMethod(list);
        int index = method.next();
        Integer keyNum = gifts.get(index);

        //测试
        Optional<WechatGold> cartOptional = wechatGoldInfoList.stream().filter(item -> item.getPrizeId().equals(keyNum)).findFirst();
        if (cartOptional.isPresent()) {
            return cartOptional.get();
        } else {
            log.info("奖品id错误：id" + keyNum);
            return null;
        }


    }

    /**
     * 拼接授权链接
     *
     * @param json 自定义数据
     * @param link 回调地址
     * @return 跳转链接
     */
    private String getOauthUrl(JSONObject json, String link) {
        String url = Module.OAUTH_URL
                + "appid=" + Module.APP_ID
                + "&redirect_uri=" + URLEncoder.encode(Module.BACK_URL + link)
                + "&response_type=code"
                + "&scope=snsapi_userinfo"
                + "&state=" + json.toJSONString() + ""
                + "#wechat_redirect";
        log.info("授权链接：" + url);
        return url;
    }

    /**
     * 创建领取记录
     *
     * @param tokenPhone 登录信息
     * @param userInfo   用户信息
     * @param amount     金额
     * @param factory    id工厂
     * @return 领取记录
     */
    private CollectRecords createCollectRecords(String tokenPhone, WechatUserInfo userInfo, Double amount, SnowflakeIdFactory factory) {
        String recordsId = String.valueOf(factory.nextId());
        CollectRecords collectRecords = new CollectRecords();
        collectRecords.setRecordsId(recordsId);
        collectRecords.setPhone(tokenPhone);
        collectRecords.setPrizeName("微信立减金");
        collectRecords.setPrizeImg("/ynbocfollow/yonghu/images/jiangpin1.png");
        collectRecords.setType(PrizeType.WECHAT_GOLD);
        collectRecords.setRecordsDate(new Date());
        collectRecords.setStatus(2);
        collectRecords.setUserId(userInfo.getUserId());

        collectRecords.setAmount(new BigDecimal(amount));
        collectRecords = collectRecordsService.save(collectRecords);
        return collectRecords;
    }

    /**
     * 添加邀请记录
     *
     * @param userInfo 用户信息
     * @param status   添加状态
     */
    private void addInvitationRecord(WechatUserInfo userInfo, Integer status) {
        SnowflakeIdFactory factory = new SnowflakeIdFactory(SnowflakeIdFactory.getWorkerId(), 1L);
        if (StringUtils.hasLength(userInfo.getSubscribeScene())) {
            if (Module.ADD_SCENE_QR_CODE.equals(userInfo.getSubscribeScene())) {
                if (StringUtils.hasLength(userInfo.getQrSceneStr())) {
                    Optional<WechatUserInfo> invited = userService.findByUserId(userInfo.getQrSceneStr());
                    if (invited.isPresent()) {
                        String inviteId = String.valueOf(factory.nextId());
                        invitationRecordService.add(inviteId, new Date(), invited.get().getUserId(), userInfo.getUserId(), userInfo.getQrScene(), status);
                        if ("1001".equals(userInfo.getQrScene())) {
                            //用户 2-已领取立减金
                            if (status == 2) {
                                LotteryNumber lotteryNumber = lotteryNumberService.findByUserId(invited.get().getUserId());
                                if (lotteryNumber == null) {
                                    lotteryNumber = new LotteryNumber();
                                    String lotteryId = String.valueOf(factory.nextId());
                                    lotteryNumber.setLotteryId(lotteryId);
                                    lotteryNumber.setUserId(userInfo.getUserId());
                                    lotteryNumber.setPhone(userInfo.getActivityMobile());
                                    lotteryNumber.setTotalSurplusNumber(0);
                                    lotteryNumber.setInvitationTotal(0);
                                    lotteryNumber.setSurplusNumber(0);
                                    lotteryNumber.setInvitationSurplus(0);
                                }
                                lotteryNumber.setTotalSurplusNumber(lotteryNumber.getTotalSurplusNumber() + 1);
                                lotteryNumber.setInvitationSurplus(lotteryNumber.getInvitationSurplus() + 1);
                                if (lotteryNumber.getInvitationSurplus() >= 1) {
                                    lotteryNumber.setInvitationSurplus(0);
                                    lotteryNumber.setTotalSurplusNumber(lotteryNumber.getTotalSurplusNumber() + 1);
                                    lotteryNumber.setSurplusNumber(lotteryNumber.getSurplusNumber() + 1);
                                }
                                lotteryNumber = lotteryNumberService.save(lotteryNumber);
                            }

                        } else if ("1002".equals(userInfo.getQrScene())) {
                            //员工
                            LotteryNumber lotteryNumber = lotteryNumberService.findByUserId(invited.get().getUserId());
                            if (lotteryNumber == null) {
                                lotteryNumber = new LotteryNumber();
                                String lotteryId = String.valueOf(factory.nextId());
                                lotteryNumber.setLotteryId(lotteryId);
                                lotteryNumber.setUserId(userInfo.getUserId());
                                lotteryNumber.setPhone(userInfo.getActivityMobile());
                                lotteryNumber.setTotalSurplusNumber(0);
                                lotteryNumber.setInvitationTotal(0);
                                lotteryNumber.setSurplusNumber(0);
                                lotteryNumber.setInvitationSurplus(0);
                            }
                            lotteryNumber.setTotalSurplusNumber(lotteryNumber.getTotalSurplusNumber() + 1);
                            lotteryNumber.setInvitationSurplus(lotteryNumber.getInvitationSurplus() + 1);
                            if (lotteryNumber.getInvitationSurplus() >= 1) {
                                lotteryNumber.setInvitationSurplus(0);
                                lotteryNumber.setTotalSurplusNumber(lotteryNumber.getTotalSurplusNumber() + 1);
                                lotteryNumber.setSurplusNumber(lotteryNumber.getSurplusNumber() + 1);
                            }
                            lotteryNumber = lotteryNumberService.save(lotteryNumber);
                        }
                    }
                }
            }
        }
    }


    private String getLjjUrl(int type) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amountType", 1);

        //TODO TYPE
//        jsonObject.put("amountType", type);
        jsonObject.put("voucherKey", "uTjOkNtmFrd1mn7trM8tUCvLG9UzXj2G");
        String url = null;
        try {
            url = "https://wechat.zhongyunkj.cn/wxljj_v2/v3/getCoupon?body=" + AESUtil.Encrypt(jsonObject.toJSONString(), AESUtil.AES_SKEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String httpGet = OkhttpUtil.getInstance().httpGet(url);
        log.info("httpGet: {}",httpGet);
        jsonObject = JSON.parseObject(httpGet);
        Integer status = jsonObject.getInteger("status");
        if (status != 200) {
            return "";
        }
        String ljjUrl = jsonObject.getString("index");
        try {
            ljjUrl = ljjUrl.replace(ljjUrl.substring(ljjUrl.indexOf("=") + 1), AESUtil.Encrypt(ljjUrl.substring(ljjUrl.indexOf("=") + 1), AESUtil.AES_SKEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(ljjUrl);
        return ljjUrl;
    }


}
