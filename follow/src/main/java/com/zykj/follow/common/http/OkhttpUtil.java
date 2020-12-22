package com.zykj.follow.common.http;

import okhttp3.*;
import okhttp3.internal.platform.Platform;

import java.io.IOException;

/**
 * okhttp工具类
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-12-03
 */

public class OkhttpUtil {


    private volatile static OkhttpUtil mInstance;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;
    private Request.Builder builder;


    public static OkhttpUtil getInstance() {
        return initClient(null);
    }

    private OkhttpUtil(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }
        mPlatform = Platform.get();
    }

    private static OkhttpUtil initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (OkhttpUtil.class) {
                if (mInstance == null) {
                    mInstance = new OkhttpUtil(okHttpClient);
                }
            }
        }
        return mInstance;
    }


    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    public String httpGet(String url) {
        String result = null;

        Request request = new Request.Builder().url(url).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public  String httpPost(String url, String data) {
        String result = null;
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            Response response = httpClient.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
