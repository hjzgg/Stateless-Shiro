package com.hjzgg.stateless.shiroWeb.controller;

import com.alibaba.fastjson.JSONObject;
import com.hjzgg.stateless.common.constants.AuthConstants;
import com.hjzgg.stateless.common.response.Result;
import com.hjzgg.stateless.common.utils.InvocationInfoProxy;
import com.hjzgg.stateless.shiroWeb.annotation.CustomCrossOrigin;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "index")
@CustomCrossOrigin
public class IndexController {
	
	private Logger logger = LoggerFactory.getLogger(IndexController.class);

	@ApiOperation(value = "web表单登录", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "authority", value = "认证串信息", required = true, dataType = "string", paramType = "header"),
	})
	@RequestMapping(value = "user", method = RequestMethod.GET)
	public Result user() {
		String cuser = null;
		if (SecurityUtils.getSubject().getPrincipal() != null) {
			cuser = (String) SecurityUtils.getSubject().getPrincipal();
		}
		logger.debug("cuser is {}", cuser);
		JSONObject data = new JSONObject();
		data.put("userName", InvocationInfoProxy.getUserName());
		data.put("token", InvocationInfoProxy.getToken());
		data.put("loginTs", InvocationInfoProxy.getLoginTs());
		data.put("userType", InvocationInfoProxy.getParameter(AuthConstants.ExtendConstants.PARAM_USERTYPE));
		return Result.success(data);
	}
}