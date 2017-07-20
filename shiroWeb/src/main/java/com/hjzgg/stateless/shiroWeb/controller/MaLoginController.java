package com.hjzgg.stateless.shiroWeb.controller;

import com.alibaba.fastjson.JSONObject;
import com.hjzgg.stateless.auth.token.ITokenProcessor;
import com.hjzgg.stateless.auth.token.TokenParameter;
import com.hjzgg.stateless.common.constants.AuthConstants;
import com.hjzgg.stateless.common.response.Result;
import com.hjzgg.stateless.common.utils.MapToStringUtil;
import com.hjzgg.stateless.shiroWeb.annotation.CustomCrossOrigin;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 移动端默认登录逻辑
 */
@RestController
@CustomCrossOrigin
@RequestMapping(value = "login")
public class MaLoginController {
	
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
	//移动的指定为maTokenProcessor
	@Autowired
	protected ITokenProcessor maTokenProcessor;

	@ApiOperation(value = "ma表单登录", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "用户标识", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "password", value = "用户密码", required = true, dataType = "string", paramType = "query"),
	})
	@RequestMapping(value="maLogin", method = RequestMethod.POST)
	public Object formLogin(HttpServletRequest request) throws IOException {
		String userName = request.getParameter("username");
        String passWord = request.getParameter("password");

		if (passWord != null && userName != null) {
			// 模拟用户
			if("admin".equals(userName) && passWord.equals("admin")){
				TokenParameter tp = new TokenParameter();
				//设置用户标识
				tp.setUserName("admin");
				//设置登录时间
				tp.setLoginTs(String.valueOf(System.currentTimeMillis()));
				tp.getExt().put(AuthConstants.ExtendConstants.PARAM_USERTYPE, "2");
				Cookie[] cookies = maTokenProcessor.getCookieFromTokenParameter(tp);

				JSONObject result = new JSONObject();
				for(Cookie cookie : cookies){
					result.put(cookie.getName(), cookie.getValue());
				}

				return Result.success(MapToStringUtil.toEqualString(result, ';'));
			} else {
				logger.error("用户名密码错误!");
				return Result.failure("用户名密码错误!");
			}
		} else {
			logger.error("用户名密码为空!");
			return Result.failure("用户名密码为空!");
		}
	}
}
