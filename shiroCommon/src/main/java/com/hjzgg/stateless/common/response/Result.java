package com.hjzgg.stateless.common.response;

import java.lang.reflect.Field;

public class Result {

    private String code;

    private String message;

    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private static Result newInstance() {
        return new Result();
    }

    public static Result success(Object data) {
        Result result = newInstance();
        result.setData(data);
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMessage(ResultCodeEnum.SUCCESS.getMsg());

        if (data == null) {
            result.setData("");
        } else {
            Field[] fields = data.getClass().getDeclaredFields();// 遇到没有属性的空类,防止JSON转换的时候异常
            if (fields.length == 0) {
                result.setData("");
            }
        }
        return result;
    }

    public static Result failure(String message) {
        Result result = newInstance();
        result.setCode(ResultCodeEnum.FAILURE.getCode());
        result.setMessage(message);
        return result;
    }

    public static Result Result(String errorCode, String message) {
        Result result = newInstance();
        result.setCode(errorCode);
        result.setMessage(message);
        return result;
    }

    public static Result failure(String errorCode, String message, Object data) {
        Result result = newInstance();
        result.setCode(errorCode);
        result.setMessage(message);
        result.setData(data);

        if (data == null) {
            result.setData("");
        }
        return result;
    }
}
