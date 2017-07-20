package com.hjzgg.stateless.shiroSimpleWeb;

import com.alibaba.fastjson.JSONObject;
import com.hjzgg.stateless.shiroSimpleWeb.codec.HmacSHA256Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Created by hujunzheng on 2017/6/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config-shiro.xml", "classpath:spring-mvc.xml"})
@WebAppConfiguration
public class TestController {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private void requestAndOutput(String url, String params) throws Exception {
        String responseString = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .accept(MediaType.ALL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(params)
        )//.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())         //打印出请求和相应的内容
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(responseString);
    }

    private void requestAndOutput(String url, JSONObject params) throws Exception {
        MockHttpServletRequestBuilder query = MockMvcRequestBuilders.post(url)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);

        params.forEach((key, value) -> {
            query.param(key, (String)value);
        });

        String responseString = mockMvc.perform(query)
//              .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())         //打印出请求和相应的内容
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(responseString);
    }

    @Before
    public void setUp( ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test() throws Exception {
        String username = "admin";
        String param11 = "param1";
        String param2 = "param2";
        String key = "dadadswdewq2ewdwqdwadsadasd";
        JSONObject params = new JSONObject();
        params.put(Constants.PARAM_USERNAME, username);
        params.put("param1", param11);
        params.put("param2", param2);
        params.put(Constants.PARAM_DIGEST, HmacSHA256Utils.digest(key, params));

        requestAndOutput("/hello", params);
    }
}
