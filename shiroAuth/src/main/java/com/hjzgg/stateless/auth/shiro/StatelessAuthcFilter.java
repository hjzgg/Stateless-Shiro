package com.hjzgg.stateless.auth.shiro;

import com.alibaba.fastjson.JSONObject;
import com.hjzgg.stateless.auth.token.ITokenProcessor;
import com.hjzgg.stateless.auth.token.TokenFactory;
import com.hjzgg.stateless.auth.token.TokenParameter;
import com.hjzgg.stateless.common.constants.AuthConstants;
import com.hjzgg.stateless.common.utils.CookieUtil;
import com.hjzgg.stateless.common.utils.InvocationInfoProxy;
import com.hjzgg.stateless.common.utils.MapToStringUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

public class StatelessAuthcFilter extends AccessControlFilter {
	
	private static final Logger log = LoggerFactory.getLogger(StatelessAuthcFilter.class);

	public static final int HTTP_STATUS_AUTH = 306;

	@Value("${filterExclude}")
	private String exeludeStr;

	@Autowired
	private TokenFactory tokenFactory;
	
	private String[] esc = new String[] {
		"/logout","/login","/formLogin",".jpg",".png",".gif",".css",".js",".jpeg"
	};

	private List<String> excludCongtextKeys = new ArrayList<>();
	
	public void setTokenFactory(TokenFactory tokenFactory) {
		this.tokenFactory = tokenFactory;
	}

	public void setEsc(String[] esc) {
		this.esc = esc;
	}
	
	public void setExcludCongtextKeys(List<String> excludCongtextKeys) {
		this.excludCongtextKeys = excludCongtextKeys;
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

		boolean isAjax = isAjax(request);

		// 1、客户端发送来的摘要
		HttpServletRequest hReq = (HttpServletRequest) request;
		HttpServletRequest httpRequest = hReq;
		Cookie[] cookies = httpRequest.getCookies();
		String authority = httpRequest.getHeader("Authority");
		
		//如果header中包含，则以header为主，否则，以cookie为主
		if(StringUtils.isNotBlank(authority)){
			Set<Cookie> cookieSet = new HashSet<Cookie>();
			String[] ac = authority.split(";");
			for(String s : ac){
				String[] cookieArr = s.split("=");
				String key = StringUtils.trim(cookieArr[0]);
				String value = StringUtils.trim(cookieArr[1]);
				Cookie cookie = new Cookie(key, value);
				cookieSet.add(cookie);
			}
			cookies = cookieSet.toArray(new Cookie[]{});
		}
		
		String tokenStr = CookieUtil.findCookieValue(cookies, AuthConstants.PARAM_TOKEN);
		String cookieUserName = CookieUtil.findCookieValue(cookies, AuthConstants.PARAM_USERNAME);

		String loginTs = CookieUtil.findCookieValue(cookies, AuthConstants.PARAM_LOGINTS);

		// 2、客户端传入的用户身份
		String userName = request.getParameter(AuthConstants.PARAM_USERNAME);
		if (userName == null && StringUtils.isNotBlank(cookieUserName)) {
			userName = cookieUserName;
		}

		boolean needCheck = !include(hReq);

		if (needCheck) {
			if (StringUtils.isEmpty(tokenStr) || StringUtils.isEmpty(userName)) {
				if (isAjax) {
					onAjaxAuthFail(request, response);
				} else {
					onLoginFail(request, response);
				}
				return false;
			}

			// 3、客户端请求的参数列表
			Map<String, String[]> params = new HashMap<String, String[]>(request.getParameterMap());

			ITokenProcessor tokenProcessor = tokenFactory.getTokenProcessor(tokenStr);
			TokenParameter tp = tokenProcessor.getTokenParameterFromCookie(cookies);
			// 4、生成无状态Token
			StatelessToken token = new StatelessToken(userName, tokenProcessor, tp, params, new String(tokenStr));

			try {
				// 5、委托给Realm进行登录
				getSubject(request, response).login(token); // 这个地方应该验证上下文信息中的正确性

				// 设置上下文变量
				InvocationInfoProxy.setUserName(userName);
				InvocationInfoProxy.setLoginTs(loginTs);
				InvocationInfoProxy.setToken(tokenStr);

				//设置上下文携带的额外属性
				initExtendParams(cookies);

				initMDC();
				afterValidate(hReq);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				if (isAjax && e instanceof AuthenticationException) {
					onAjaxAuthFail(request, response); // 6、验证失败，返回ajax调用方信息
					return false;
				} else {
					onLoginFail(request, response); // 6、登录失败，跳转到登录页
					return false;
				}
			}
			return true;
		} else {
			return true;
		}

	}

	private boolean isAjax(ServletRequest request) {
		boolean isAjax = false;
		if (request instanceof HttpServletRequest) {
			HttpServletRequest rq = (HttpServletRequest) request;
			String requestType = rq.getHeader("X-Requested-With");
			if (requestType != null && "XMLHttpRequest".equals(requestType)) {
				isAjax = true;
			}
		}
		return isAjax;
	}

	protected void onAjaxAuthFail(ServletRequest request, ServletResponse resp) throws IOException {
		HttpServletResponse response = (HttpServletResponse) resp;
		JSONObject json = new JSONObject();
		json.put("msg", "auth check error!");
		response.setStatus(HTTP_STATUS_AUTH);
		response.getWriter().write(json.toString());
	}

	// 登录失败时默认返回306状态码
	protected void onLoginFail(ServletRequest request, ServletResponse response) throws IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setStatus(HTTP_STATUS_AUTH);
		request.setAttribute("msg", "auth check error!");
		// 跳转到登录页
		redirectToLogin(request, httpResponse);
	}

	@Override
	protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		HttpServletRequest hReq = (HttpServletRequest) request;
		String rURL = hReq.getRequestURI();
		String errors = StringUtils.isEmpty((String) request.getAttribute("msg")) ? "" : "&msg=" + request.getAttribute("msg");

		if(request.getAttribute("msg") != null) {
			rURL += ((StringUtils.isNotEmpty(hReq.getQueryString())) ?
					"&" : "") + "msg=" + request.getAttribute("msg");
		}

		rURL = Base64.encodeBase64URLSafeString(rURL.getBytes()) ;
		// 加入登录前地址, 以及错误信息
		String loginUrl = getLoginUrl() + "?r=" + rURL + errors;

		WebUtils.issueRedirect(request, response, loginUrl);
	}

	public boolean include(HttpServletRequest request) {
		String u = request.getRequestURI();
		for (String e : esc) {
			if (u.endsWith(e)) {
				return true;
			}
		}

		if(StringUtils.isNotBlank(exeludeStr)){
			String[] customExcludes = exeludeStr.split(",");
			for (String e : customExcludes) {
				if (u.endsWith(e)) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception) throws Exception {
		super.afterCompletion(request, response, exception);
		InvocationInfoProxy.reset();
		clearMDC();
	}

	// 设置上下文中的扩展参数，rest传递上下文时生效，Authority header中排除固定key的其它信息都设置到InvocationInfoProxy的parameters
	private void initExtendParams(Cookie[] cookies) {
		for (Cookie cookie : cookies) {
			String cname = cookie.getName();
			String cvalue = cookie.getValue();
			if(!excludCongtextKeys.contains(cname)){
				InvocationInfoProxy.setParameter(cname, cvalue);
			}
		}
	}
	
	private void initMDC() {
		String userName = "";
		Subject subject = SecurityUtils.getSubject();
		if (subject != null && subject.getPrincipal() != null) {
			userName = (String) SecurityUtils.getSubject().getPrincipal();
		}

		// MDC中记录用户信息
		MDC.put(AuthConstants.PARAM_USERNAME, userName);

		initCustomMDC();
	}
	
	protected void initCustomMDC() {
		MDC.put("InvocationInfoProxy", MapToStringUtil.toEqualString(InvocationInfoProxy.getResources(), ';'));
	}

	protected void afterValidate(HttpServletRequest hReq){
	}
	
	protected void clearMDC() {
		// MDC中记录用户信息
		MDC.remove(AuthConstants.PARAM_USERNAME);

		clearCustomMDC();
	}

	protected void clearCustomMDC() {
		MDC.remove("InvocationInfoProxy");
	}

	//初始化 AuthConstants类中定义的常量
	{
		Field[] fields = AuthConstants.class.getDeclaredFields();
		try {
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.getType().toString().endsWith("java.lang.String")
						&& Modifier.isStatic(field.getModifiers())) {
					excludCongtextKeys.add((String) field.get(AuthConstants.class));
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}