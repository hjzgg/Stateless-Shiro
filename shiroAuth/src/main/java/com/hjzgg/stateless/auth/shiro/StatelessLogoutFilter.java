package com.hjzgg.stateless.auth.shiro;

import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class StatelessLogoutFilter extends org.apache.shiro.web.filter.authc.LogoutFilter {
	
	private static final Logger log = LoggerFactory.getLogger(StatelessLogoutFilter.class);
	
	@Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        String redirectUrl = getRedirectUrl(request, response, subject);
        try {
            subject.logout();
            //业务上处理登出后的逻辑，如删除cookie等
            doLogout((HttpServletRequest) request, (HttpServletResponse) response);
        } catch (SessionException ise) {
            log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }
        issueRedirect(request, response, redirectUrl);
        return false;
    }

	protected void doLogout(HttpServletRequest request, HttpServletResponse response) {
	}
}
