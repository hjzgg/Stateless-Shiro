package com.hjzgg.stateless.common.response;

/**
 * Created by hujunzheng on 2017/6/6.
 */
public enum ResultCodeEnum {

    /**
     * 成功
     */
    SUCCESS("SUCCESS", "成功"),
    /**
     * 失败
     */
    FAILURE("FAIL", "失败");


    ResultCodeEnum(String code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    private String msg;

    private String code;

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

}
