package com.hjzgg.stateless.common.utils;

import com.hjzgg.stateless.common.constants.AuthConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hujunzheng on 2017/7/18.
 */
public class InvocationInfoProxy {
    private static final ThreadLocal<Map<String, Object>> resources =
        ThreadLocal.withInitial(() -> {
            Map<String, Object> initialValue = new HashMap<>();
            initialValue.put(AuthConstants.ExtendConstants.PARAM_PARAMETER, new HashMap<String, String>());
            return initialValue;
        }
    );

    public static String getUserName() {
        return (String) resources.get().get(AuthConstants.PARAM_USERNAME);
    }

    public static void setUserName(String userName) {
        resources.get().put(AuthConstants.PARAM_USERNAME, userName);
    }

    public static String getLoginTs() {
        return (String) resources.get().get(AuthConstants.PARAM_LOGINTS);
    }

    public static void setLoginTs(String loginTs) {
        resources.get().put(AuthConstants.PARAM_LOGINTS, loginTs);
    }

    public static String getToken() {
        return (String) resources.get().get(AuthConstants.PARAM_TOKEN);
    }

    public static void setToken(String token) {
        resources.get().put(AuthConstants.PARAM_TOKEN, token);
    }

    public static void setParameter(String key, String value) {
        ((Map<String, String>) resources.get().get(AuthConstants.ExtendConstants.PARAM_PARAMETER)).put(key, value);
    }

    public static String getParameter(String key) {
        return ((Map<String, String>) resources.get().get(AuthConstants.ExtendConstants.PARAM_PARAMETER)).get(key);
    }

    public static void reset() {
        resources.remove();
    }
}
