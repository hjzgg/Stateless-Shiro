package com.hjzgg.stateless.auth.shiro;

import com.hjzgg.stateless.auth.session.ShiroSessionManager;
import com.hjzgg.stateless.auth.token.ITokenProcessor;
import com.hjzgg.stateless.auth.token.TokenFactory;
import com.hjzgg.stateless.auth.token.TokenParameter;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class StatelessRealm extends AuthorizingRealm {

	private static final Logger logger = LoggerFactory.getLogger(StatelessRealm.class);

	@Autowired
	private ShiroSessionManager shiroSessionManager;

	@Override
	public boolean supports(AuthenticationToken token) {
		// 仅支持StatelessToken类型的Token
		return token instanceof StatelessToken;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		List<String> roles = new ArrayList<String>();
		info.addRoles(roles);
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken atoken) throws AuthenticationException {
		StatelessToken token = (StatelessToken) atoken;
		TokenParameter tp = token.getTp();
		String userName = (String) token.getPrincipal();
		ITokenProcessor tokenProcessor = token.getTokenProcessor();
		String tokenStr = tokenProcessor.generateToken(tp);
		if (tokenStr == null || !shiroSessionManager.validateOnlineSession(userName, tokenStr)) {
			logger.error("User [{}] authenticate fail in System, maybe session timeout!", userName);
			throw new AuthenticationException("User " + userName + " authenticate fail in System");
		}
		
		return new SimpleAuthenticationInfo(userName, tokenStr, getName());
	}

}