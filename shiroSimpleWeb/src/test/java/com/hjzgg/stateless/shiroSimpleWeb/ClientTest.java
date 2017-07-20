package com.hjzgg.stateless.shiroSimpleWeb;

import com.alibaba.fastjson.JSONObject;
import com.hjzgg.stateless.shiroSimpleWeb.codec.HmacSHA256Utils;
import com.hjzgg.stateless.shiroSimpleWeb.utils.RestTemplateUtils;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * <p>Version: 1.0
 */
public class ClientTest {

    private static final String WEB_URL = "http://localhost:8080/shiro/hello";

    @Test
    public void testServiceHelloSuccess() {
        String username = "admin";
        String param11 = "param11";
        String param12 = "param12";
        String param2 = "param2";
        String key = "dadadswdewq2ewdwqdwadsadasd";
        JSONObject params = new JSONObject();
        params.put(Constants.PARAM_USERNAME, username);
        params.put("param1", param11);
        params.put("param1", param12);
        params.put("param2", param2);
        params.put(Constants.PARAM_DIGEST, HmacSHA256Utils.digest(key, params));

        String result = RestTemplateUtils.get(WEB_URL, params);
        System.out.println(result);
    }

    @Test
    public void testServiceHelloFail() {
        String username = "admin";
        String param11 = "param11";
        String param12 = "param12";
        String param2 = "param2";
        String key = "dadadswdewq2ewdwqdwadsadasd";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add(Constants.PARAM_USERNAME, username);
        params.add("param1", param11);
        params.add("param1", param12);
        params.add("param2", param2);
        params.add(Constants.PARAM_DIGEST, HmacSHA256Utils.digest(key, params));
        params.set("param2", param2 + "1");

        String url = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080/hello")
                .queryParams(params).build().toUriString();
    }
}
