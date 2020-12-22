package com.zykj.follow.common.http;

import java.io.Serializable;

/**
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-11-27
 */

public class ServerResponse<T> implements Serializable {

    public static int OK = 200;
    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }


    public static <T> ServerResponse<T> createMessage(int code , String msg, T data){
        return new ServerResponse<T>(code,msg,data);
    }

    public static <T> ServerResponse<T> createMessage(int code , String msg){
        return new ServerResponse<T>(code,msg);
    }
}
