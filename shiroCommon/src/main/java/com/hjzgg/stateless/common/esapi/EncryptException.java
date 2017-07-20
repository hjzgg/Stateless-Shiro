package com.hjzgg.stateless.common.esapi;

/**
 * 
 * <p>
 * <b>本类主要完成以下功能：</b>
 * <p>
 * @version 1.0
 * @time 2016年4月23日 下午4:09:41
 */
public class EncryptException extends Throwable {

    public EncryptException() {
    }

    public EncryptException(String message) {
        super(message);
    }

    public EncryptException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncryptException(Throwable cause) {
        super(cause);
    }

    public EncryptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
