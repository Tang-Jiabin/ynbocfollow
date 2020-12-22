package com.zykj.follow.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zykj.follow.common.http.HttpUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HFUtils {
	public static String szAgentId = "zhongyunxj";// 用户ID
	public static String key = "cdf5102731c541d2b71e48481fe325c6";// 用户KEY
	public static String hfUrl = "https://boc.pay.zhongyunkj.cn/page/zp/hnzhuanpanV2/api/payRspsHf";


//	public static String szAgentId = "xjtest";//用户ID
//	public static String key = "xjtest";//用户KEY
	
//	public static String szAgentId = "zhongyunxj";//用户ID
//	public static String key = "cdf5102731c541d2b71e48481fe325c6";//用户KEY
//	public static String orderid = "2017121214452222";//订单号，不能重复
//	public static String phoneNum = "13522378739";//手机号

	//话费提交订单地址
	//public static String hf_submit_url = "";
	//话费测试提交地址
	public static String hf_submit_url = "http://120.55.72.146:10051/api/submitOrder.do";
	//流量提交订单地址
	public static String ll_submit_url = "";
	//话费查询订单地址
	public static String hf_query_url = "http://120.55.72.146:10521/IXJCWIQueryOrder/XJQueryOrderHF.aspx";
	//流量查询订单地址
	public static String ll_query_url = "";
	//查询余额
	public static String banlance_url = "http://120.55.72.146:10521/IXJCWIQueryBalance/XJQueryBalance.aspx";

	public static int submitOrder(String phoneNum,String orderid,String nMoney,String url,String szAgentIdZ,String keyZ) {
		 int nRtn = 9999;
		try {
			// MD5签名串
			String szTimeStamp = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

			
			String nSortType =telcom(phoneNum);
			
			String info = "szAgentId=" + szAgentIdZ// 用户ID
					+ "&szOrderId=" + orderid  //订单号
					+ "&szPhoneNum="+ phoneNum //手机号
					+ "&nMoney="+nMoney// 面值
					+ "&nSortType="+nSortType// 运营商编码，详情见接口文档
					+ "&nProductClass=1"// 产品类别，详情见接口文档
					+ "&nProductType=1"// 产品类型，详情见接口文档
					+ "&szTimeStamp=" + szTimeStamp// 当前时间
					+ "&szKey=" + keyZ;// 密钥

			// 签名小写
			String szVerifyString = DigestUtils.md5Hex(info).toLowerCase();

			
			//提交参数
			Map params = new HashMap();

			params.put("szAgentId", szAgentIdZ);
			params.put("szOrderId", orderid);
			params.put("szPhoneNum", phoneNum);
			params.put("nMoney", nMoney);
			params.put("nSortType", nSortType);
			params.put("nProductClass", 1);
			params.put("nProductType", 1);
			params.put("szTimeStamp", szTimeStamp);
			params.put("szVerifyString", szVerifyString);
			params.put("szNotifyUrl", url);
			params.put("szFormat", "JSON");


			HttpResponse response = HttpUtils.doPost(hf_submit_url, params);
			StatusLine status = response.getStatusLine();
			HttpEntity resEntity = response.getEntity();
			// 返回数据
			String result = EntityUtils.toString(resEntity);
			if (status.getStatusCode() == 200) {
				System.out.println(result);
				JSONObject object = JSON.parseObject(result);
				// 请判断nRtn状态是否提交成功
				nRtn = object.getIntValue("nRtn");
				
				/*
				 * 0 提交成功
				 */
				/*
				 * 以下可以判断提交失败 
				 * 1000系统接口维护 1001时间戳错误 1003数据格式错误 1004加密错误
				 * 2001\2002号码或号段错误 2003黑名单号码 2020\2021\2022用户信息异常
				 * 6005\6006余额信息错误 2030\2031\2032产品配置错误 1006余额不足 2043通道配置异常
				 * 3001\3002扣款失败 2040 没有配置产品 直接处理为失败
				 */
				/*
				 * 以下请查询接口确认 3003\3004扣款错误（可能是订单号重复引起的） 2051\2050生成订单异常 999 系统异常
				 */

			} else {
				System.out.println(result+"---------------");

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return nRtn;
	}
	
	
	private static String telcom(String phone) {
		  //移动
		  String isChinaMobile = "^134[0-8]\\d{7}$|^(?:13[5-9]|147|198|15[0-27-9]|178|18[2-478])\\d{8}$";
		  //联通
		  String isChinaUnion = "^(?:13[0-2]|145|15[56]|176|175|166|18[56])\\d{8}$";
		  //电信
		  String isChinaTelcom = "^(?:133|153|173|177|191|199|18[019])\\d{8}$";
		  if (phone.matches(isChinaMobile)) {
		   return "1";
		  }
		  if (phone.matches(isChinaUnion)) {
		   return "2";
		  }
		  if (phone.matches(isChinaTelcom)) {
		   return "3";
		  }
		  return "4";
		 }
	
	/**
	 * 查询订单
	 * 
	 * @throws Exception
	 *             yxw
	 */
	public static void queryOrder(String orderid) {

		try {
			//签名串
			String info = "szAgentId=" + szAgentId// 用户ID
					+ "&szOrderId=" + orderid
					+ "&szKey=" + key;// 密钥
			
			// 签名小写
			String szVerifyString = DigestUtils.md5Hex(info).toLowerCase();

			//提交参数
			Map params = new HashMap();

			params.put("szAgentId", szAgentId);
			params.put("szOrderId", orderid);
			params.put("szVerifyString", szVerifyString);
			params.put("szFormat", "JSON");
			
			

			HttpResponse response = HttpUtils.doPost(hf_query_url,params);
			StatusLine status = response.getStatusLine();
			HttpEntity resEntity = response.getEntity();
			// 返回数据
			String result = EntityUtils.toString(resEntity);
			if (status.getStatusCode() == 200) {
				System.out.println(result);
				JSONObject object = JSON.parseObject(result);
				// 请判断nRtn状态是否提交成功
				int nRtn = object.getIntValue("nRtn");

				// 返回状态。
				// 5010初始化(处理中)
				// 5011处理中（处理中）
				// 5012充值成功（成功）成功
				// 5013充值失败（失败）失败退款
				// 5019对账状态（处理中）
				// 5005无此订单（无订单）可补充或退款
				// 其余状态均不可处理为失败，需再次发起查询

			} else {
				// 请求异常
				System.out.println(result);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 查询余额 yxw
	 */
	public static void queryBanlance() {

		try {
			
			//签名串
			String info = "szAgentId=" + szAgentId// 用户ID
					+ "&szKey=" + key;// 密钥
			
			// 签名小写
			String szVerifyString = DigestUtils.md5Hex(info).toLowerCase();
			
			//提交参数
			Map params = new HashMap();

			params.put("szAgentId", szAgentId);
			params.put("szVerifyString", szVerifyString);
			params.put("szFormat", "JSON");
			

			HttpResponse response = HttpUtils.doPost(banlance_url,params);
			StatusLine status = response.getStatusLine();
			HttpEntity resEntity = response.getEntity();
			// 返回数据
			String result = EntityUtils.toString(resEntity);
			if (status.getStatusCode() == 200) {
				System.out.println(result);
				JSONObject object = JSON.parseObject(result);
				// 请判断nRtn状态是否提交成功
				int nRtn = object.getIntValue("nRtn");
				// 0查询成功,获取余额即可
				// 非0查询余额失败
				

			} else {
				// 请求异常
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

