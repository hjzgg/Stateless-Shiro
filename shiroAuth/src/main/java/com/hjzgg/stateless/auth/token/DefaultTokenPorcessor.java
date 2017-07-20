package com.hjzgg.stateless.auth.token;

import com.hjzgg.stateless.auth.session.ShiroSessionManager;
import com.hjzgg.stateless.common.constants.AuthConstants;
import com.hjzgg.stateless.common.esapi.EncryptException;
import com.hjzgg.stateless.common.esapi.IYCPESAPI;
import com.hjzgg.stateless.common.utils.CookieUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 默认Token处理器提供将cooke和TokenParameter相互转换,Token生成的能力
 * <p>
 * 可以注册多个实例
 * </p>
 * 
 * @author li
 *
 */
public class DefaultTokenPorcessor implements ITokenProcessor {
    private static Logger log = LoggerFactory.getLogger(DefaultTokenPorcessor.class);
    private static int HTTPVERSION = 3;
    static {
        URL res = DefaultTokenPorcessor.class.getClassLoader().getResource("javax/servlet/annotation/WebServlet.class");
        if (res == null) {
            HTTPVERSION = 2;
        }
    }
    private String id;
    private String domain;
    private String path = "/";
    private Integer expr;
    // 默认迭代次数
    private int hashIterations = 2;

    @Autowired
    private ShiroSessionManager shiroSessionManager;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getExpr() {
        return expr;
    }

    public void setExpr(Integer expr) {
        this.expr = expr;
    }

    private List<String> exacts = new ArrayList<String>();

    public void setExacts(List<String> exacts) {
        this.exacts = exacts;
    }

    public int getHashIterations() {
        return hashIterations;
    }

    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }

    @Override
    public String generateToken(TokenParameter tp) {
        try {
            String seed = shiroSessionManager.findSeed();
            String token = IYCPESAPI.encryptor().hash(
                            this.id + tp.getUserName() + tp.getLoginTs() + getSummary(tp) + getExpr(),
                            seed,
                            getHashIterations());
            token = this.id + "," + getExpr() + "," + token;
            return Base64.encodeBase64URLSafeString(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(token));
        } catch (EncryptException e) {
            log.error("TokenParameter is not validate!", e);
            throw new IllegalArgumentException("TokenParameter is not validate!");
        }
    }

    @Override
    public Cookie[] getCookieFromTokenParameter(TokenParameter tp) {
        List<Cookie> cookies = new ArrayList<Cookie>();
        String tokenStr = generateToken(tp);
        Cookie token = new Cookie(AuthConstants.PARAM_TOKEN, tokenStr);
        if (HTTPVERSION == 3)
            token.setHttpOnly(true);
        if (StringUtils.isNotEmpty(domain))
            token.setDomain(domain);
        token.setPath(path);
        cookies.add(token);

        try {
            Cookie userId = new Cookie(AuthConstants.PARAM_USERNAME, URLEncoder.encode(tp.getUserName(), "UTF-8"));
            if (StringUtils.isNotEmpty(domain))
                userId.setDomain(domain);
            userId.setPath(path);
            cookies.add(userId);

            // 登录的时间戳
            Cookie logints = new Cookie(AuthConstants.PARAM_LOGINTS, URLEncoder.encode(tp.getLoginTs(), "UTF-8"));
            if (StringUtils.isNotEmpty(domain))
                logints.setDomain(domain);
            logints.setPath(path);
            cookies.add(logints);
        } catch (UnsupportedEncodingException e) {
            log.error("encode error!", e);
        }

        if (!tp.getExt().isEmpty()) {
            Iterator<Entry<String, String>> it = tp.getExt().entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> i = it.next();
                Cookie ext = new Cookie(i.getKey(), i.getValue());
                if (StringUtils.isNotEmpty(domain))
                    ext.setDomain(domain);
                ext.setPath(path);
                cookies.add(ext);
            }
        }

        shiroSessionManager.registOnlineSession(tp.getUserName(), tokenStr, this);

        return cookies.toArray(new Cookie[] {});
    }

    @Override
    public TokenParameter getTokenParameterFromCookie(Cookie[] cookies) {
        TokenParameter tp = new TokenParameter();
        String token = CookieUtil.findCookieValue(cookies, AuthConstants.PARAM_TOKEN);
        TokenInfo ti = TokenFactory.getTokenInfo(token);
        if (ti.getIntegerExpr().intValue() != this.getExpr().intValue()) {
            throw new IllegalArgumentException("illegal token!");
        }
        String userId = CookieUtil.findCookieValue(cookies, AuthConstants.PARAM_USERNAME);
        tp.setUserName(userId);
        String loginTs = CookieUtil.findCookieValue(cookies, AuthConstants.PARAM_LOGINTS);
        tp.setLoginTs(loginTs);

        if (exacts != null && !exacts.isEmpty()) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                String name = cookie.getName();
                if (exacts.contains(name)) {
                    tp.getExt().put(name,
                            cookie.getValue() == null ? "" : cookie.getValue());
                }
            }
        }
        return tp;
    }

    protected String getSummary(TokenParameter tp) {
        if (exacts != null && !exacts.isEmpty()) {
            int len = exacts.size();
            String[] exa = new String[len];
            for (int i = 0; i < len; i++) {
                String name = exacts.get(i);
                String value = tp.getExt().get(name);
                if(value == null) value = "";
                exa[i] = value;
            }
            return StringUtils.join(exa, "#");
        }
        return "";
    }

    @Override
    public Cookie[] getLogoutCookie(String tokenStr, String uid) {
        List<Cookie> cookies = new ArrayList<Cookie>();
        Cookie token = new Cookie(AuthConstants.PARAM_TOKEN, null);
        if (StringUtils.isNotEmpty(domain))
            token.setDomain(domain);
        token.setPath(path);
        cookies.add(token);

        Cookie userId = new Cookie(AuthConstants.PARAM_USERNAME, null);
        if (StringUtils.isNotEmpty(domain))
            userId.setDomain(domain);
        userId.setPath(path);
        cookies.add(userId);

        // 登录的时间戳
        Cookie logints = new Cookie(AuthConstants.PARAM_LOGINTS, null);
        if (StringUtils.isNotEmpty(domain))
            logints.setDomain(domain);
        logints.setPath(path);
        cookies.add(logints);
        for (String exact : exacts) {
            Cookie ext = new Cookie(exact, null);
            if (StringUtils.isNotEmpty(domain))
                ext.setDomain(domain);
            ext.setPath(path);
            cookies.add(ext);
        }

        shiroSessionManager.delOnlineSession(uid, tokenStr);

        return cookies.toArray(new Cookie[] {});
    }
}
