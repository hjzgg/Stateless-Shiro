package com.hjzgg.stateless.shiroWeb.controller;

import com.hjzgg.stateless.auth.token.ITokenProcessor;
import com.hjzgg.stateless.auth.token.TokenParameter;
import com.hjzgg.stateless.common.constants.AuthConstants;
import com.hjzgg.stateless.common.response.Result;
import com.hjzgg.stateless.common.utils.MapToStringUtil;
import com.hjzgg.stateless.shiroWeb.annotation.CustomCrossOrigin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

/**
 * 默认登录逻辑
 */
@RestController
@RequestMapping(value = "login")
@CustomCrossOrigin
@Api
public class WebLoginController {
	
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping(method = RequestMethod.GET)
    public Result login(HttpServletRequest request) {
		return Result.success(MapToStringUtil.maptoMapString(request.getParameterMap()));
	}


	//为网页版本的登录Controller指定webTokenProcessor 相应的移动的指定为maTokenProcessor
	@Autowired
	protected ITokenProcessor webTokenProcessor;

	@ApiOperation(value = "web表单登录", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "用户标识", required = true, dataType = "string", paramType = "form"),
			@ApiImplicitParam(name = "password", value = "用户密码", required = true, dataType = "string", paramType = "form"),
	})
	@RequestMapping(value="webLogin", method = RequestMethod.POST)
	public String formLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        		tp.getExt().put(AuthConstants.ExtendConstants.PARAM_USERTYPE, "1");
                Cookie[] cookies = webTokenProcessor.getCookieFromTokenParameter(tp);
                for(Cookie cookie : cookies){
            	    response.addCookie(cookie);
                }
            } else {
            	logger.error("用户名密码错误!");
                return "login";
            }
            return "redirect";
		} else {
            return "login";
		}
	}
	
}
