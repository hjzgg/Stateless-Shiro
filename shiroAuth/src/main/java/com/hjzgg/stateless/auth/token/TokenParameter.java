package com.hjzgg.stateless.auth.token;

import java.util.HashMap;
import java.util.Map;

public class TokenParameter {
	
	private String userName;
	
	private String loginTs;
	 
	Map<String, String> ext = new HashMap<String, String>();
	
	public Map<String, String> getExt() {
		return ext;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLoginTs() {
		return loginTs;
	}

	public void setLoginTs(String loginTs) {
		this.loginTs = loginTs;
	}
}
