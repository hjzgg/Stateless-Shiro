package com.hjzgg.stateless.common.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.servlet.http.Cookie;

public class CookieUtil {
	public static Cookie createCookie(String key, String value) {
		return createCookie(key, value, true);
	}

	public static Cookie createCookie(String key, String value, boolean httpOnly) {
		Cookie cookie = new Cookie(key, URLEncoder.encode(value));
		cookie.setHttpOnly(httpOnly);
		cookie.setMaxAge(-1);
		cookie.setPath("/");
		return cookie;
	}

	public static String findCookieValue(Cookie[] cookies, String key) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					return URLDecoder.decode(cookie.getValue());
				}
			}
		}
		return null;
	}

	public static Cookie expireCookieWithPath(String key, String path) {
		Cookie cookie = new Cookie(key, null);
		cookie.setMaxAge(0);
		cookie.setPath(path);
		return cookie;
	}
}