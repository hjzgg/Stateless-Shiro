package com.hjzgg.stateless.common.esapi;


/**
 * 
 * <p>
 * <b>ESAPI工具类,提供编码/解码，加密/解密，签名</b>
 * <p>
 * @version 1.0
 * @time 2016年4月23日 下午3:53:02
 */
public class IYCPESAPI {

    private static final IYCPEncryptor encryptUtils = new IYCPEncryptor();

    private static final IYCPEncoder encoderUtils = new IYCPEncoder();

    /**
     * 获得加解密工具类
     *
     * @return
     */
    public static IYCPEncryptor encryptor() {
        return encryptUtils;
    }

    /**
     * 获得编码工具类
     * @return
     */
    public static IYCPEncoder encoder() {
        return encoderUtils;
    }


}
