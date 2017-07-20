package com.hjzgg.stateless.auth.token;

import javax.servlet.http.Cookie;

/**
 * Token处理器提供Token生成 将cooke和TokenParameter相互转换的能力
 * 
 * @author licza@yonyou.com
 *
 */
public interface ITokenProcessor {
	/**
	 * 返回Token处理器ID.
	 * 
	 * @return
	 */
	String getId();
	/**
	 * 返回Token过期时间
	 * @return
	 */
	Integer getExpr();

	/**
	 * 生成Token.
	 * 
	 * @param tp
	 * @return
	 */
	String generateToken(TokenParameter tp);

	/**
	 * 根据token参数生成Cookie.
	 *
	 * @param tp
	 * @return
	 */
	Cookie[] getCookieFromTokenParameter(TokenParameter tp);

	/**
	 * 获取签名属性列表
	 * @return
	 */
	Cookie[] getLogoutCookie(String token, String userid);


	/**
	 * 从Cookie中还原Token参数.
	 *
	 * @param cookies
	 * @return
	 */
	TokenParameter getTokenParameterFromCookie(Cookie[] cookies);
}
