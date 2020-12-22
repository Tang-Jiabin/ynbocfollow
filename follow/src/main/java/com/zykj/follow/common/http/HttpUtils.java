package com.zykj.follow.common.http;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * HTTP 请求工具类
 */
@Slf4j
public class HttpUtils {


	private static RequestConfig requestConfig;
	private static final int MAX_TIMEOUT = 60000;

	static {

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(MAX_TIMEOUT);
		// 设置读取超时
		configBuilder.setSocketTimeout(MAX_TIMEOUT);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		// 在提交请求之前 测试连接是否可用
		//        configBuilder.setStaleConnectionCheckEnabled(true);
		requestConfig = configBuilder.build();
	}

	public static String requestAsHttpGET(String urlvalue) {
		String inputLine = "";
		try {
			URL url = new URL(urlvalue);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
			urlConnection.setReadTimeout(100000);
			urlConnection.setConnectTimeout(100000);
			String line = in.readLine();
			while (line != null) {
				inputLine += line;
				line = in.readLine();
			}
		} catch (SSLHandshakeException d) {
			requestAsHttpGET(urlvalue);
			d.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
		log.debug("\rHTTP:" + urlvalue + "\rRETURN:" + inputLine);
		return inputLine;
	}
	
	/**
	 * 发送 POST 请求（HTTP），不带输入数据
	 * @param apiUrl
	 * @return
	 */
	public static HttpResponse doPost(String apiUrl) {
		return doPost(apiUrl, new HashMap<String, Object>());
	}

	/**
	 * 发送 POST 请求（HTTP），K-V形式
	 * @param apiUrl API接口URL
	 * @param params 参数map
	 * @return
	 */
	public static HttpResponse doPost(String apiUrl, Map<String, Object> params) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(apiUrl);
		CloseableHttpResponse response = null;

		try {
			httpPost.setConfig(requestConfig);
			List<NameValuePair> pairList = new ArrayList<>(params.size());
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
						.getValue().toString());
				pairList.add(pair);
			}
			httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
			response = httpClient.execute(httpPost);

		} catch (IOException e) {

			throw new RuntimeException(e);
		}
		return response;
	}
	

}