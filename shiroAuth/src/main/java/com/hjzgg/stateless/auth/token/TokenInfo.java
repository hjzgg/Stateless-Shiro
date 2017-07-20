package com.hjzgg.stateless.auth.token;

import java.io.Serializable;

/**
 * 解析出的token
 */
public class TokenInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4365301079641325282L;
	String token;
	String processor;
	String expr;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getProcessor() {
		return processor;
	}
	public void setProcessor(String processor) {
		this.processor = processor;
	}
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	
	public Integer getIntegerExpr(){
		return Integer.parseInt(getExpr());
	}
}
