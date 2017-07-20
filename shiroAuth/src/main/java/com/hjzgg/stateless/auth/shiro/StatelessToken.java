package com.hjzgg.stateless.auth.shiro;

import com.hjzgg.stateless.auth.token.ITokenProcessor;
import com.hjzgg.stateless.auth.token.TokenParameter;
import org.apache.shiro.authc.AuthenticationToken;

import java.util.Map;

public class StatelessToken implements AuthenticationToken {

	private String userName;
	// 预留参数集合，校验更复杂的权限
    private Map<String, ?> params;
    private String clientDigest;
    ITokenProcessor tokenProcessor;
    TokenParameter tp;
    public StatelessToken(String userName, ITokenProcessor tokenProcessor, TokenParameter tp , Map<String, ?> params, String clientDigest) {
        this.userName = userName;
        this.params = params;
        this.tp = tp;
        this.tokenProcessor = tokenProcessor;
        this.clientDigest = clientDigest;
    }

    public TokenParameter getTp() {
		return tp;
	}

	public void setTp(TokenParameter tp) {
		this.tp = tp;
	}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public  Map<String, ?> getParams() {
        return params;
    }

    public void setParams( Map<String, ?> params) {
        this.params = params;
    }

    public String getClientDigest() {
        return clientDigest;
    }

    public void setClientDigest(String clientDigest) {
        this.clientDigest = clientDigest;
    }

    @Override
    public Object getPrincipal() {
       return userName;
    }

    @Override
    public Object getCredentials() {
        return clientDigest;
    }

	public ITokenProcessor getTokenProcessor() {
		return tokenProcessor;
	}

	public void setTokenProcessor(ITokenProcessor tokenProcessor) {
		this.tokenProcessor = tokenProcessor;
	}
}