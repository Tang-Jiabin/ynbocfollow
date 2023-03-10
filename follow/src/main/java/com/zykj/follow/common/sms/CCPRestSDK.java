/*
 *  Copyright (c) 2014 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.zykj.follow.common.sms;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

public class CCPRestSDK {

	private static final Logger LOGGER = LoggerFactory.getLogger(CCPRestSDK.class);

	int status;
	private static final int Request_Get = 0;

	private static final int Request_Post = 1;
	private static final String Account_Info = "AccountInfo";
	private static final String Create_SubAccount = "SubAccounts";
	private static final String Get_SubAccounts = "GetSubAccounts";
	private static final String Query_SubAccountByName = "QuerySubAccountByName";

	private static final String SMSMessages = "SMS/Messages";
	private static final String TemplateSMS = "SMS/TemplateSMS";
	private static final String Query_SMSTemplate = "SMS/QuerySMSTemplate";
	private static final String LandingCalls = "Calls/LandingCalls";
	private static final String VoiceVerify = "Calls/VoiceVerify";
	private static final String IvrDial = "ivr/dial";
	private static final String BillRecords = "BillRecords";
	private static final String queryCallState = "ivr/call";
	private static final String callResult = "CallResult";
	private static final String mediaFileUpload = "Calls/MediaFileUpload";
//	private String SERVER_IP;
//	private String SERVER_PORT;
//	private String ACCOUNT_SID;
//	private String ACCOUNT_TOKEN;
//	private String SUBACCOUNT_SID;
//	private String SUBACCOUNT_Token;
//	public String App_ID;
	
	private String SERVER_IP="app.cloopen.com";
	private String SERVER_PORT="8883";
	private String ACCOUNT_SID="8a216da86b8863a1016b9248fa7006fa";
	private String ACCOUNT_TOKEN="ada5cc7b7ccc4d79ae3bdcbd58d2f183";
	private String SUBACCOUNT_SID;
	private String SUBACCOUNT_Token;
	public String App_ID="8aaf07086b8862cb016b96baab4709b2";
	
	private BodyType BODY_TYPE = BodyType.Type_XML;
	public String Callsid;

	public enum BodyType {
		Type_XML, Type_JSON;
	}

	public enum AccountType {
		Accounts, SubAccounts;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param serverIP
	 *            ???????????? ???????????????
	 * @param serverPort
	 *            ???????????? ???????????????
	 */
	public void init(String serverIP, String serverPort) {
		if (isEmpty(serverIP) || isEmpty(serverPort)) {
			LOGGER.error("???????????????:serverIP???serverPort??????");
			throw new IllegalArgumentException("????????????:"
					+ (isEmpty(serverIP) ? " ??????????????? " : "")
					+ (isEmpty(serverPort) ? " ??????????????? " : "") + "??????");
		}
		SERVER_IP = serverIP;
		SERVER_PORT = serverPort;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param accountSid
	 *            ???????????? ?????????
	 * @param accountToken
	 *            ???????????? ?????????TOKEN
	 */
	public void setAccount(String accountSid, String accountToken) {
		if (isEmpty(accountSid) || isEmpty(accountToken)) {
			LOGGER.error("???????????????:accountSid???accountToken??????");
			throw new IllegalArgumentException("????????????:"
					+ (isEmpty(accountSid) ? " ?????????" : "")
					+ (isEmpty(accountToken) ? " ?????????TOKEN " : "") + "??????");
		}
		ACCOUNT_SID = accountSid;
		ACCOUNT_TOKEN = accountToken;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param subAccountSid
	 *            ???????????? ?????????
	 * @param subAccountToken
	 *            ???????????? ?????????TOKEN
	 */
	public void setSubAccount(String subAccountSid, String subAccountToken) {
		if (isEmpty(subAccountSid) || isEmpty(subAccountToken)) {
			LOGGER.error("???????????????:subAccountSid???subAccountToken??????");
			throw new IllegalArgumentException("????????????:"
					+ (isEmpty(subAccountSid) ? " ?????????" : "")
					+ (isEmpty(subAccountToken) ? " ?????????TOKEN " : "") + "??????");
		}
		SUBACCOUNT_SID = subAccountSid;
		SUBACCOUNT_Token = subAccountToken;
	}

	/**
	 * ???????????????Id
	 * 
	 * @param appId
	 *            ???????????? ??????Id
	 */
	public void setAppId(String appId) {
		if (isEmpty(appId)) {
			LOGGER.error("???????????????:appId??????");
			throw new IllegalArgumentException("????????????: ??????Id ??????");
		}
		App_ID = appId;
	}


	/**
	 * ????????????
	 * 
	 * @param date
	 *            ???????????? day ??????????????????????????????00:00 ??? 23:59???
	 * @param keywords
	 *            ???????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????
	 * @return
	 */
	public HashMap<String, Object> billRecords(String date, String keywords) {

		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(date))) {
			LOGGER.error("????????????: ??????  ??????");
			throw new IllegalArgumentException("????????????: ??????  ??????");
		}
		//// ?????? 2
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			//e1.printStackTrace();
			LOGGER.error(e1.getMessage());
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, BillRecords);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("date", date);
				if (!(isEmpty(keywords)))
					json.addProperty("keywords", keywords);

				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><BillRecords>");
				sb.append("<appId>").append(App_ID).append("</appId>")
						.append("<date>").append(date).append("</date>");
				if (!(isEmpty(keywords)))
					sb.append("<keywords>").append(keywords)
							.append("</keywords>");

				sb.append("</BillRecords>").toString();
				requsetbody = sb.toString();
			}
			LOGGER.info("billRecords Request body = : " + requsetbody);
			//????????????
			System.out.println("??????????????????"+requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);	
			
			status = response.getStatusLine().getStatusCode();
			
			System.out.println("Https????????????????????????"+status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LOGGER.info("billRecords response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}

	/**
	 * ??????IVR????????????
	 * 
	 * @param number
	 *            ???????????? ?????????????????????Dial???????????????
	 * @param userdata
	 *            ???????????? ??????????????????<startservice>???????????????????????????????????????????????????Dial???????????????
	 * @param record
	 *            ???????????? ???????????????????????????true???false???????????????false???????????????Dial???????????????
	 * @param disnumber
	 *            ???????????? ???????????????????????????????????????????????????????????????
	 * @return
	 */
	public HashMap<String, Object> ivrDial(String number, String userdata,
			boolean record,String disnumber) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if (isEmpty(number)) {
			LOGGER.error("????????????: ???????????????   ??????");
			throw new IllegalArgumentException("????????????: ???????????????   ??????");
		}
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, IvrDial);
			String requsetbody = "";

			StringBuilder sb = new StringBuilder(
					"<?xml version='1.0' encoding='utf-8'?><Request>");
			sb.append("<Appid>").append(App_ID).append("</Appid>")
					.append("<Dial number=").append("\"").append(number)
					.append("\"");
			if (record) {
				sb.append(" record=").append("\"").append(record).append("\"");
			}
			if (userdata != null) {
				sb.append(" userdata=").append("\"").append(userdata)
						.append("\"");
			}
			if (disnumber != null) {
				sb.append(" disnumber=").append("\"").append(disnumber)
						.append("\"");
			}
			sb.append("></Dial></Request>").toString();
			requsetbody = sb.toString();

			LOGGER.info("ivrDial Request body = : " + requsetbody);
			System.out.println("??????????????????"+requsetbody);
			
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();

			System.out.println("Https????????????????????????"+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LOGGER.info("ivrDial response body = " + result);
		try {
			return xmlToMap(result);
		} catch (Exception e) {
			return getMyError("172003", "??????????????????");
		}
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param verifyCode
	 *            ???????????? ????????????????????????????????????????????????????????????????????????4-8???
	 * @param to
	 *            ???????????? ????????????
	 * @param displayNum
	 *            ???????????? ???????????????????????????????????????????????????
	 * @param playTimes
	 *            ???????????? ?????????????????????1???3??????????????????1???
	 * @param respUrl
	 *            ???????????? ??????????????????????????????????????????????????????????????????Url??????????????????????????????
	 * @param lang
	 *            ???????????? ????????????
	 * @param userData
	 *            ???????????? ?????????????????????
	 * @param welcomePrompt
	 * 			     ???????????? wav??????????????????????????????????????????????????????????????????????????????????????????verifyCode???????????????????????????playVerifyCode???????????????
	 * @param playVerifyCode 
	 *            ???????????? wav????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????verifyCode???????????????????????????????????????????????????????????????playVerifyCode???
	 * @param maxCallTime 
	 * 			     ???????????? ?????????????????? 
	 * @return
	 */
	public HashMap<String, Object> voiceVerify(String verifyCode, String to,
			String displayNum, String playTimes, String respUrl, String lang,
			String userData, String welcomePrompt, String playVerifyCode, String maxCallTime) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(verifyCode)) || (isEmpty(to)))
			throw new IllegalArgumentException("????????????:"
					+ (isEmpty(verifyCode) ? " ??????????????? " : "")
					+ (isEmpty(to) ? " ???????????? " : "") + "??????");
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, VoiceVerify);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("verifyCode", verifyCode);
				json.addProperty("to", to);
				if (!(isEmpty(displayNum)))
					json.addProperty("displayNum", displayNum);

				if (!(isEmpty(playTimes)))
					json.addProperty("playTimes", playTimes);

				if (!(isEmpty(respUrl)))
					json.addProperty("respUrl", respUrl);
				if (!(isEmpty(lang)))
					json.addProperty("lang", lang);
				if (!(isEmpty(userData)))
					json.addProperty("userData", userData);
				if (!(isEmpty(welcomePrompt)))
					json.addProperty("welcomePrompt", welcomePrompt);
				if (!(isEmpty(playVerifyCode)))
					json.addProperty("playVerifyCode", playVerifyCode);
				if (!(isEmpty(maxCallTime)))
					json.addProperty("maxCallTime", maxCallTime);

				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><VoiceVerify>");
				sb.append("<appId>").append(App_ID).append("</appId>")
						.append("<verifyCode>").append(verifyCode)
						.append("</verifyCode>").append("<to>").append(to)
						.append("</to>");
				if (!(isEmpty(displayNum)))
					sb.append("<displayNum>").append(displayNum)
							.append("</displayNum>");

				if (!(isEmpty(playTimes)))
					sb.append("<playTimes>").append(playTimes)
							.append("</playTimes>");

				if (!(isEmpty(respUrl)))
					sb.append("<respUrl>").append(respUrl).append("</respUrl>");
				if (!(isEmpty(lang)))
					sb.append("<lang>").append(lang).append("</lang>");
				if (!(isEmpty(userData)))
					sb.append("<userData>").append(userData)
							.append("</userData>");
				if (!(isEmpty(welcomePrompt)))
					sb.append("<welcomePrompt>").append(welcomePrompt)
							.append("</welcomePrompt>");
				if (!(isEmpty(playVerifyCode)))
					sb.append("<playVerifyCode>").append(playVerifyCode)
							.append("</playVerifyCode>");
				if (!(isEmpty(maxCallTime)))
					sb.append("<maxCallTime>").append(maxCallTime)
							.append("</maxCallTime>");

				sb.append("</VoiceVerify>").toString();
				requsetbody = sb.toString();
			}

			LOGGER.info("voiceVerify Request body = : " + requsetbody);
			System.out.println("??????????????????"+requsetbody);
			
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);			
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https????????????????????????"+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		LOGGER.info("voiceVerify response body = " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}

	/**
	 * ????????????????????????
	 * 
	 * @param to
	 *            ???????????? ????????????
	 * @param mediaName
	 *            ???????????? ??????????????????????????? wav??????mediaTxt?????????????????????????????????mediaTxt????????????
	 * @param mediaTxt
	 *            ???????????? ??????????????????????????????
	 * @param displayNum
	 *            ???????????? ??????????????????????????????????????????????????????
	 * @param playTimes
	 *            ???????????? ?????????????????????1???3??????????????????1???
	 * @param respUrl
	 *            ???????????? ???????????????????????????????????????????????????????????????Url??????????????????????????????
	 * @param userData
	 *            ???????????? ??????????????????
	 * @param txtSpeed
	 *            ???????????? ???????????????????????????????????????????????????-500???500??????mediaTxt??????????????????????????????0???
	 * @param txtVolume
	 *            ???????????? ???????????????????????????????????????????????????-20???20??????mediaTxt??????????????????????????????0???
	 * @param txtPitch
	 *            ???????????? ?????????????????????????????????????????????-500???500??????mediaTxt??????????????????????????????0???
	 * @param txtBgsound
	 *            ???????????? ??????????????????????????????????????????????????????????????????6???????????????1???6???????????????????????????0????????????????????????????????????????????????????????????????????????mediaTxt?????????????????? 
	 * @param playMode
	 *            ???????????? ??????????????????????????????????????? , 0?????? 1???????????????0????????????????????????
	 * @return
	 */
	public HashMap<String, Object> landingCall(String to, String mediaName,
		    String mediaTxt, String displayNum, String playTimes,
			String respUrl,String userData, String txtSpeed, String txtVolume,
			String txtPitch, String txtBgsound, String playMode) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if (isEmpty(to))
			throw new IllegalArgumentException("????????????:"
					+ (isEmpty(to) ? " ???????????? " : "") + "??????");
		if ((isEmpty(mediaName)) && (isEmpty(mediaTxt)))
			throw new IllegalArgumentException("?????????????????????????????????????????????????????????????????????");
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, LandingCalls);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("to", to);

				if (!(isEmpty(mediaName)))
					json.addProperty("mediaName", mediaName);

				if (!(isEmpty(mediaTxt)))
					json.addProperty("mediaTxt", mediaTxt);

				if (!(isEmpty(displayNum)))
					json.addProperty("displayNum", displayNum);
				if (!(isEmpty(playTimes)))
					json.addProperty("playTimes", playTimes);

				if (!(isEmpty(respUrl)))
					json.addProperty("respUrl", respUrl);
				if (!(isEmpty(userData)))
					json.addProperty("userData", userData);
				if (!(isEmpty(txtSpeed)))
					json.addProperty("txtSpeed", txtSpeed);
				if (!(isEmpty(txtVolume)))
					json.addProperty("txtVolume", txtVolume);
				if (!(isEmpty(txtPitch)))
					json.addProperty("txtPitch", txtPitch);
				if (!(isEmpty(txtBgsound)))
					json.addProperty("txtBgsound", txtBgsound);
				if (!(isEmpty(playMode)))
					json.addProperty("playMode", playMode);

				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><LandingCall>");
				sb.append("<appId>").append(App_ID).append("</appId>")
						.append("<to>").append(to).append("</to>");
				if (!(isEmpty(mediaName)))
					sb.append("<mediaName>").append(mediaName)
							.append("</mediaName>");
				else if (!(isEmpty(mediaName)))
					sb.append("<mediaName>").append(mediaName)
							.append("</mediaName>");

				if (!(isEmpty(mediaTxt)))
					sb.append("<mediaTxt>").append(mediaTxt)
							.append("</mediaTxt>");

				if (!(isEmpty(displayNum)))
					sb.append("<displayNum>").append(displayNum)
							.append("</displayNum>");
				if (!(isEmpty(playTimes)))
					sb.append("<playTimes>").append(playTimes)
							.append("</playTimes>");

				if (!(isEmpty(respUrl)))
					sb.append("<respUrl>").append(respUrl).append("</respUrl>");
				if (!(isEmpty(userData)))
					sb.append("<userData>").append(userData).append("</userData>");
				if (!(isEmpty(txtSpeed)))
					sb.append("<txtSpeed>").append(txtSpeed).append("</txtSpeed>");
				if (!(isEmpty(txtVolume)))
					sb.append("<txtVolume>").append(txtVolume).append("</txtVolume>");
				if (!(isEmpty(txtPitch)))
					sb.append("<txtPitch>").append(txtPitch).append("</txtPitch>");
				if (!(isEmpty(txtBgsound)))
					sb.append("<txtBgsound>").append(txtBgsound).append("</txtBgsound>");
				if (!(isEmpty(playMode)))
					sb.append("<playMode>").append(playMode).append("</playMode>");

				sb.append("</LandingCall>").toString();
				requsetbody = sb.toString();
			}
			LOGGER.info("landingCalls Request body = : " + requsetbody);
			
			System.out.println("??????????????????"+requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https????????????????????????"+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		LOGGER.info("landingCall response body = " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}

	/**
	 * ????????????????????????
	 * 
	 * @param to
	 *            ???????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????100???
	 * @param templateId
	 *            ???????????? ??????Id
	 * @param datas
	 *            ???????????? ????????????????????????????????????{??????}
	 * @return
	 */
	public HashMap<String, Object> sendTemplateSMS(String to,
			String templateId, String[] datas) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(to)) || (isEmpty(App_ID)) || (isEmpty(templateId)))
			throw new IllegalArgumentException("????????????:"
					+ (isEmpty(to) ? " ???????????? " : "")
					+ (isEmpty(templateId) ? " ??????Id " : "") + "??????");

		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, TemplateSMS);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("to", to);
				json.addProperty("templateId", templateId);
				if (datas != null) {
					StringBuilder sb = new StringBuilder("[");
					for (String s : datas) {
						sb.append("\"" + s + "\"" + ",");
					}
					sb.replace(sb.length() - 1, sb.length(), "]");
					JsonParser parser = new JsonParser();
					JsonArray Jarray = parser.parse(sb.toString())
							.getAsJsonArray();
					json.add("datas", Jarray);
				}
				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><TemplateSMS>");
				sb.append("<appId>").append(App_ID).append("</appId>")
						.append("<to>").append(to).append("</to>")
						.append("<templateId>").append(templateId)
						.append("</templateId>");
				if (datas != null) {
					sb.append("<datas>");
					for (String s : datas) {
						sb.append("<data>").append(s).append("</data>");
					}
					sb.append("</datas>");
				}
				sb.append("</TemplateSMS>").toString();
				requsetbody = sb.toString();
			}
			//????????????
//			System.out.println("??????????????????"+requsetbody);
			LOGGER.info("sendTemplateSMS Request body =  " + requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			
			HttpResponse response = httpclient.execute(httppost);
			
			
			//???????????????
			
			status = response.getStatusLine().getStatusCode();
			
//			System.out.println("Https????????????????????????"+status);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????"+"Https??????????????????"+status);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

//		LOGGER.info("sendTemplateSMS response body = " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}

	/**
	 * ?????????????????????
	 * 
	 *            ???????????? ??????Id
	 * @param friendlyName
	 *            ???????????? ???????????????
	 * @return
	 */
	public HashMap<String, Object> querySubAccount(String friendlyName) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(friendlyName))) {
			LOGGER.error("????????????: ??????????????? ??????");
			throw new IllegalArgumentException("????????????: ??????????????? ??????");
		}
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1,
					Query_SubAccountByName);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("friendlyName", friendlyName);
				requsetbody = json.toString();
			} else {
				requsetbody = "<?xml version='1.0' encoding='utf-8'?><SubAccount>"
						+ "<appId>"
						+ App_ID
						+ "</appId>"
						+ "<friendlyName>"
						+ friendlyName + "</friendlyName>" + "</SubAccount>";
			}
			LOGGER.info("querySubAccountByName Request body =  "
					+ requsetbody);
			System.out.println("??????????????????"+requsetbody);
			
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https????????????????????????"+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		LOGGER.info("querySubAccount result " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}

	}

	/**
	 * ???????????????
	 * 
	 * @param startNo
	 *            ???????????? ???????????????????????????0??????
	 * @param offset
	 *            ???????????? ???????????????????????????????????????1???????????????100???
	 * @return
	 */
	public HashMap<String, Object> getSubAccounts(String startNo, String offset) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1,
					Get_SubAccounts);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				if (!(isEmpty(startNo)))
					json.addProperty("startNo", startNo);
				if (!(isEmpty(offset)))
					json.addProperty("offset", offset);
				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><SubAccount>");
				sb.append("<appId>").append(App_ID).append("</appId>");
				if (!(isEmpty(startNo)))
					sb.append("<startNo>").append(startNo).append("</startNo>");
				if (!(isEmpty(offset)))
					sb.append("<offset>").append(offset).append("</offset>");
				sb.append("</SubAccount>").toString();
				requsetbody = sb.toString();
			}
			LOGGER.info("GetSubAccounts Request body =  " + requsetbody);
			System.out.println("??????????????????"+requsetbody);
			
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https????????????????????????"+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LOGGER.info("getSubAccounts result " + result);

		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}

	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	public HashMap<String, Object> queryAccountInfo() {
		if ((isEmpty(SERVER_IP))) {
			return getMyError("172004", "IP??????");
		}
		if ((isEmpty(SERVER_PORT))) {
			return getMyError("172005", "????????????");
		}
		if ((isEmpty(ACCOUNT_SID))) {
			return getMyError("172006", "???????????????");
		}
		if ((isEmpty(ACCOUNT_TOKEN))) {
			return getMyError("172007", "?????????TOKEN??????");
		}

		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LOGGER.error(e1.getMessage());
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpGet httpGet = (HttpGet) getHttpRequestBase(0, Account_Info);
			HttpResponse response = httpclient.execute(httpGet);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https????????????????????????"+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LOGGER.info("queryAccountInfo response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}

	/**
	 * ???????????????
	 * 
	 * @param friendlyName
	 *            ???????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * @return
	 */
	public HashMap<String, Object> createSubAccount(String friendlyName) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if (isEmpty(friendlyName)) {
			LOGGER.error("????????????: ??????????????? ??????");
			throw new IllegalArgumentException("????????????: ??????????????? ??????");
		}

		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LOGGER.error(e1.getMessage());
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1,
					Create_SubAccount);
			String requsetbody = "";

			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("friendlyName", friendlyName);
				requsetbody = json.toString();
			} else {
				requsetbody = "<?xml version='1.0' encoding='utf-8'?><SubAccount>"
						+ "<appId>"
						+ App_ID
						+ "</appId>"
						+ "<friendlyName>"
						+ friendlyName + "</friendlyName>" + "</SubAccount>";
			}
			LOGGER.info("CreateSubAccount Request body =  " + requsetbody);
			System.out.println("??????????????????"+requsetbody);
			
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);

			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();


			System.out.println("Https????????????????????????"+status);
			
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LOGGER.info("createSubAccount response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @param templateId
	 *            ???????????? ??????Id??????????????????????????????????????????
	 * @return
	 */
	public HashMap<String, Object> QuerySMSTemplate(String templateId) {
		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;

		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LOGGER.error(e1.getMessage());
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1,
					Query_SMSTemplate);
			String requsetbody = "";

			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				json.addProperty("appId", App_ID);
				json.addProperty("templateId", templateId);
				requsetbody = json.toString();
			} else {
				requsetbody = "<?xml version='1.0' encoding='utf-8'?><Request>"
						+ "<appId>" + App_ID + "</appId>" + "<templateId>"
						+ templateId + "</templateId>" + "</Request>";
			}
			LOGGER.info("QuerySMSTemplate Request body =  " + requsetbody);
			//????????????
			System.out.println("??????????????????"+requsetbody);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);

			HttpResponse response = httpclient.execute(httppost);
		
			//???????????????
			
			status = response.getStatusLine().getStatusCode();
			
			System.out.println("Https????????????????????????"+status);

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				result = EntityUtils.toString(entity, "UTF-8");
			}

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LOGGER.info("QuerySMSTemplate response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}
	/**
	 * ??????????????????
	 * 
	 * @param callid
	 *            ???????????? ??????Id
	 * @param action
	 *            ???????????? ???????????????????????????url??????
	 * @return
	 */
	public HashMap<String, Object> QueryCallState(String callid, String action) {

		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(callid))) {
			LOGGER.error("????????????: callid  ??????");
			throw new IllegalArgumentException("????????????: callid ??????");
		}
		Callsid = callid;
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LOGGER.error(e1.getMessage());
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, queryCallState);
			String requsetbody = "";
			if (BODY_TYPE == BodyType.Type_JSON) {
				JsonObject json = new JsonObject();
				JsonObject json2 = new JsonObject();
				json.addProperty("Appid", App_ID);
				json2.addProperty("callid", callid);
				if (!(isEmpty(action)))
					json2.addProperty("action", action);
				json.addProperty("QueryCallState", json2.toString());
				requsetbody = json.toString();
			} else {
				StringBuilder sb = new StringBuilder(
						"<?xml version='1.0' encoding='utf-8'?><Request>");
				sb.append("<Appid>").append(App_ID).append("</Appid>")
				.append("<QueryCallState callid=").append("\"").append(callid)
				.append("\"");
				if (action != null) {
					sb.append(" action=").append("\"").append(action)
							.append("\"").append("/");
				}

				sb.append("></Request>").toString();
				requsetbody = sb.toString();
			}
			LOGGER.info("queryCallState Request body = : " + requsetbody);
			System.out.println("??????????????????"+requsetbody);
			
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody
					.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https????????????????????????"+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LOGGER.info("billRecords response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @param callSid
	 *            ???????????? ??????Id
	 * @return
	 */
	public HashMap<String, Object> CallResult(String callSid) {
		if ((isEmpty(SERVER_IP))) {
			return getMyError("172004", "IP??????");
		}
		if ((isEmpty(SERVER_PORT))) {
			return getMyError("172005", "????????????");
		}
		if ((isEmpty(ACCOUNT_SID))) {
			return getMyError("172006", "???????????????");
		}
		if ((isEmpty(ACCOUNT_TOKEN))) {
			return getMyError("172007", "?????????TOKEN??????");
		}
		Callsid = callSid;
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LOGGER.error(e1.getMessage());
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpGet httpGet = (HttpGet) getHttpRequestBase(0, callResult);
			HttpResponse response = httpclient.execute(httpGet);
			
			status = response.getStatusLine().getStatusCode();
		
			System.out.println("Https????????????????????????"+status);
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LOGGER.info("queryAccountInfo response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @param filename
	 *            ???????????? ?????????
	 * @param fis
	 *            ???????????? ??????????????????
	 * @return
	 */
	public String Filename;
	public HashMap<String, Object> MediaFileUpload(String filename, FileInputStream fis) {

		HashMap<String, Object> validate = accountValidate();
		if (validate != null)
			return validate;
		if ((isEmpty(filename))) {
			LOGGER.error("????????????: filename  ??????");
			throw new IllegalArgumentException("????????????: filename ??????");
		}
		if(fis==null){
			LOGGER.error("????????????: fis  ??????");
			throw new IllegalArgumentException("????????????????????????");
		}

		Filename=filename;
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = new DefaultHttpClient();	
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e1) {
			e1.printStackTrace();
			LOGGER.error(e1.getMessage());
			throw new RuntimeException("?????????httpclient??????" + e1.getMessage());
		}
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1,mediaFileUpload);

			LOGGER.info("MediaFileUpload Request body = : " + fis);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(fis);
			requestBody.setContentLength(fis.available());
			System.out.println("??????????????????"+requestBody);
			
			httppost.setEntity(requestBody);
			HttpResponse response = httpclient.execute(httppost);			
			status = response.getStatusLine().getStatusCode();
			System.out.println("Https????????????????????????"+status);
			
			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172001", "????????????");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			return getMyError("172002", "?????????");
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		LOGGER.info("billRecords response body = " + result);
		try {
			if (BODY_TYPE == BodyType.Type_JSON) {
				return jsonToMap(result);
			} else {
				return xmlToMap(result);
			}
		} catch (Exception e) {

			return getMyError("172003", "??????????????????");
		}
	}

	private HashMap<String, Object> jsonToMap(String result) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		JsonParser parser = new JsonParser();
		JsonObject asJsonObject = parser.parse(result).getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = asJsonObject.entrySet();
		HashMap<String, Object> hashMap2 = new HashMap<String, Object>();

		for (Entry<String, JsonElement> m : entrySet) {
			if ("statusCode".equals(m.getKey())
					|| "statusMsg".equals(m.getKey()))
				hashMap.put(m.getKey(), m.getValue().getAsString());
			else {
				if ("SubAccount".equals(m.getKey())
						|| "totalCount".equals(m.getKey())
						|| "smsTemplateList".equals(m.getKey())
						|| "token".equals(m.getKey())
						|| "callSid".equals(m.getKey())
						|| "state".equals(m.getKey())
						|| "downUrl".equals(m.getKey())) {
					if (!"SubAccount".equals(m.getKey())
							&& !"smsTemplateList".equals(m.getKey()))
						hashMap2.put(m.getKey(), m.getValue().getAsString());
					else {
						try {
							if ((m.getValue().toString().trim().length() <= 2)
									&& !m.getValue().toString().contains("[")) {
								hashMap2.put(m.getKey(), m.getValue()
										.getAsString());
								hashMap.put("data", hashMap2);
								break;
							}
							if (m.getValue().toString().contains("[]")) {
								hashMap2.put(m.getKey(), new JsonArray());
								hashMap.put("data", hashMap2);
								continue;
							}
							JsonArray asJsonArray = parser.parse(
									m.getValue().toString()).getAsJsonArray();
							ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
							for (JsonElement j : asJsonArray) {
								Set<Entry<String, JsonElement>> entrySet2 = j
										.getAsJsonObject().entrySet();
								HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
								for (Entry<String, JsonElement> m2 : entrySet2) {
									hashMap3.put(m2.getKey(), m2.getValue()
											.getAsString());
								}
								arrayList.add(hashMap3);
							}
							hashMap2.put(m.getKey(), arrayList);
						} catch (Exception e) {
							JsonObject asJsonObject2 = parser.parse(
									m.getValue().toString()).getAsJsonObject();
							Set<Entry<String, JsonElement>> entrySet2 = asJsonObject2
									.entrySet();
							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Entry<String, JsonElement> m2 : entrySet2) {
								hashMap3.put(m2.getKey(), m2.getValue()
										.getAsString());
							}
							hashMap2.put(m.getKey(), hashMap3);
							hashMap.put("data", hashMap2);
						}

					}
					hashMap.put("data", hashMap2);
				} else {

					JsonObject asJsonObject2 = parser.parse(
							m.getValue().toString()).getAsJsonObject();
					Set<Entry<String, JsonElement>> entrySet2 = asJsonObject2
							.entrySet();
					HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
					for (Entry<String, JsonElement> m2 : entrySet2) {
						hashMap3.put(m2.getKey(), m2.getValue().getAsString());
					}
					if (hashMap3.size() != 0) {
						hashMap2.put(m.getKey(), hashMap3);
					} else {
						hashMap2.put(m.getKey(), m.getValue().getAsString());
					}
					hashMap.put("data", hashMap2);
				}
			}
		}
		return hashMap;
	}

	/**
	 * @description ???xml??????????????????map
	 * @param xml
	 * @return Map
	 */
	private HashMap<String, Object> xmlToMap(String xml) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml); // ??????????????????XML
			Element rootElt = doc.getRootElement(); // ???????????????
			HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
			ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
			for (Iterator i = rootElt.elementIterator(); i.hasNext();) {
				Element e = (Element) i.next();
				if ("statusCode".equals(e.getName())
						|| "statusMsg".equals(e.getName()))
					map.put(e.getName(), e.getText());
				else {
					if ("SubAccount".equals(e.getName())
							|| "TemplateSMS".equals(e.getName())
							|| "totalCount".equals(e.getName())
							|| "token".equals(e.getName())
							|| "callSid".equals(e.getName())
							|| "state".equals(e.getName())
							|| "downUrl".equals(e.getName())) {
						if (!"SubAccount".equals(e.getName())&&!"TemplateSMS".equals(e.getName())) {
							hashMap2.put(e.getName(), e.getText());
						} else if ("SubAccount".equals(e.getName())){

							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Iterator i2 = e.elementIterator(); i2
									.hasNext();) {
								Element e2 = (Element) i2.next();
								hashMap3.put(e2.getName(), e2.getText());
							}
							arrayList.add(hashMap3);
							hashMap2.put("SubAccount", arrayList);
						}else if ("TemplateSMS".equals(e.getName())){

							HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
							for (Iterator i2 = e.elementIterator(); i2
									.hasNext();) {
								Element e2 = (Element) i2.next();
								hashMap3.put(e2.getName(), e2.getText());
							}
							arrayList.add(hashMap3);
							hashMap2.put("TemplateSMS", arrayList);
						}
						map.put("data", hashMap2);
					} else {

						HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
						for (Iterator i2 = e.elementIterator(); i2.hasNext();) {
							Element e2 = (Element) i2.next();
							// hashMap2.put(e2.getName(),e2.getText());
							hashMap3.put(e2.getName(), e2.getText());
						}
						if (hashMap3.size() != 0) {
							hashMap2.put(e.getName(), hashMap3);
						} else {
							hashMap2.put(e.getName(), e.getText());
						}
						map.put("data", hashMap2);
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	private HttpRequestBase getHttpRequestBase(int get, String action)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return getHttpRequestBase(get, action, AccountType.Accounts);
	}

	private HttpRequestBase getHttpRequestBase(int get, String action,
			AccountType mAccountType) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		String timestamp = DateUtil.dateToStr(new Date(),
				DateUtil.DATE_TIME_NO_SLASH);
		EncryptUtil eu = new EncryptUtil();
		String sig = "";
		String acountName = "";
		String acountType = "";
		if (mAccountType == AccountType.Accounts) {
			acountName = ACCOUNT_SID;
			sig = ACCOUNT_SID + ACCOUNT_TOKEN + timestamp;
			acountType = "Accounts";
		} else {
			acountName = SUBACCOUNT_SID;
			sig = SUBACCOUNT_SID + SUBACCOUNT_Token + timestamp;
			acountType = "SubAccounts";
		}
		String signature = eu.md5Digest(sig);

		String url = getBaseUrl().append("/" + acountType + "/")
				.append(acountName).append("/" + action + "?sig=")
				.append(signature).toString();
		if (callResult.equals(action)) {
			url = url + "&callsid=" + Callsid;
		}
		if (queryCallState.equals(action)) {
			url = url + "&callid=" + Callsid;
		}
		if (mediaFileUpload.equals(action)) {
			url = url + "&appid=" + App_ID + "&filename=" + Filename;
		}
		LOGGER.info(getmethodName(action) + " url = " + url);
		//System.out.println(getmethodName(action) + " url = " + url);
		HttpRequestBase mHttpRequestBase = null;
		if (get == Request_Get)
			mHttpRequestBase = new HttpGet(url);
		else if (get == Request_Post)
			mHttpRequestBase = new HttpPost(url);
		if (IvrDial.equals(action)) {
			setHttpHeaderXML(mHttpRequestBase);
		} else if (mediaFileUpload.equals(action)) {
			setHttpHeaderMedia(mHttpRequestBase);
		} else {
			setHttpHeader(mHttpRequestBase);
		}

		String src = acountName + ":" + timestamp;

		String auth = eu.base64Encoder(src);
		mHttpRequestBase.setHeader("Authorization", auth);
		System.out.println("?????????Url???"+mHttpRequestBase);//??????Url
		return mHttpRequestBase;
		
	}

	private String getmethodName(String action) {
		if (action.equals(Account_Info)) {
			return "queryAccountInfo";
		} else if (action.equals(Create_SubAccount)) {
			return "createSubAccount";
		} else if (action.equals(Get_SubAccounts)) {
			return "getSubAccounts";
		} else if (action.equals(Query_SubAccountByName)) {
			return "querySubAccount";
		} else if (action.equals(SMSMessages)) {
			return "sendSMS";
		} else if (action.equals(TemplateSMS)) {
			return "sendTemplateSMS";
		} else if (action.equals(LandingCalls)) {
			return "landingCalls";
		} else if (action.equals(VoiceVerify)) {
			return "voiceVerify";
		} else if (action.equals(IvrDial)) {
			return "ivrDial";
		} else if (action.equals(BillRecords)) {
			return "billRecords";
		} else {
			return "";
		}
	}

	private void setHttpHeaderXML(AbstractHttpMessage httpMessage) {
		httpMessage.setHeader("Accept", "application/xml");
		httpMessage.setHeader("Content-Type", "application/xml;charset=utf-8");
	}
	
	private void setHttpHeaderMedia(AbstractHttpMessage httpMessage) {
		if (BODY_TYPE == BodyType.Type_JSON) {
			httpMessage.setHeader("Accept", "application/json");
			httpMessage.setHeader("Content-Type", "application/octet-stream;charset=utf-8;");
		} else {
			httpMessage.setHeader("Accept", "application/xml");
			httpMessage.setHeader("Content-Type", "application/octet-stream;charset=utf-8;");
		}
	}

	private void setHttpHeader(AbstractHttpMessage httpMessage) {
		if (BODY_TYPE == BodyType.Type_JSON) {
			httpMessage.setHeader("Accept", "application/json");
			httpMessage.setHeader("Content-Type",
					"application/json;charset=utf-8");
			
		} else {
			httpMessage.setHeader("Accept", "application/xml");
			httpMessage.setHeader("Content-Type",
					"application/xml;charset=utf-8");
		}
	}

	private StringBuffer getBaseUrl() {
		StringBuffer sb = new StringBuffer("https://");
		sb.append(SERVER_IP).append(":").append(SERVER_PORT);
		sb.append("/2013-12-26");
		return sb;
	}

	private boolean isEmpty(String str) {
		return (("".equals(str)) || (str == null));
	}

	private HashMap<String, Object> getMyError(String code, String msg) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("statusCode", code);
		hashMap.put("statusMsg", msg);
		return hashMap;
	}

	private HashMap<String, Object> subAccountValidate() {
		if ((isEmpty(SERVER_IP))) {
			return getMyError("172004", "IP??????");
		}
		if ((isEmpty(SERVER_PORT))) {
			return getMyError("172005", "????????????");
		}
		if (isEmpty(SUBACCOUNT_SID))
			return getMyError("172008", "????????????");
		if (isEmpty(SUBACCOUNT_Token))
			return getMyError("172009", "?????????TOKEN???");
		return null;
	}

	private HashMap<String, Object> accountValidate() {
		if ((isEmpty(SERVER_IP))) {
			return getMyError("172004", "IP??????");
		}
		if ((isEmpty(SERVER_PORT))) {
			return getMyError("172005", "????????????");
		}
		if ((isEmpty(ACCOUNT_SID))) {
			return getMyError("172006", "???????????????");
		}
		if ((isEmpty(ACCOUNT_TOKEN))) {
			return getMyError("172007", "?????????TOKEN??????");
		}
		if ((isEmpty(App_ID))) {
			return getMyError("172012", "??????ID??????");
		}
		return null;
	}

	private void setBodyType(BodyType bodyType) {
		BODY_TYPE = bodyType;
	}
	
}