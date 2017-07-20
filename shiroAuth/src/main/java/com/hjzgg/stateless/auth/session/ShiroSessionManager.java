package com.hjzgg.stateless.auth.session;

import com.hjzgg.stateless.auth.token.ITokenProcessor;
import com.hjzgg.stateless.auth.token.TokenFactory;
import com.hjzgg.stateless.auth.token.TokenGenerator;
import com.hjzgg.stateless.common.cache.RedisCacheTemplate;
import com.hjzgg.stateless.common.esapi.EncryptException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ShiroSessionManager {

	@Autowired
	private RedisCacheTemplate redisCacheTemplate;

	@Value("${sessionMutex}")
	private boolean sessionMutex = false;

	public static final String TOKEN_SEED = "token_seed";
	
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static String localSeedValue = null;

	/**
     * 获得当前系统的 token seed
     */
    public String findSeed() throws EncryptException {
    	if(localSeedValue != null){
    		return localSeedValue;
    	} else {
    		String seed = getSeedValue(TOKEN_SEED);
    		if (StringUtils.isBlank(seed)) {
    			seed = TokenGenerator.genSeed();
    			localSeedValue = seed;
				redisCacheTemplate.put(TOKEN_SEED, seed);
    		}
    		return seed;
    	}
    }
    
    public String getSeedValue(String key) {
    	return (String) redisCacheTemplate.get(key);
    }


    /**
     * 删除session缓存
     * 
     * @param sid mock的sessionid
     */
    public void removeSessionCache(String sid) {
    	redisCacheTemplate.delete(sid);
    }


    private int getTimeout(String sid){
    	return TokenFactory.getTokenInfo(sid).getIntegerExpr();
    }

    private String getCurrentTimeSeconds() {
    	return String.valueOf(System.currentTimeMillis()/1000);
	}
    
	public void registOnlineSession(final String userName, final String token, final ITokenProcessor processor) {
		final String key = userName;
		logger.debug("token processor id is {}, key is {}, sessionMutex is {}!" , processor.getId(), key, sessionMutex);

		// 是否互斥，如果是，则踢掉所有当前用户的session，重新创建，此变量将来从配置文件读取
		if(sessionMutex){
			deleteUserSession(key);
		} else {
			// 清理此用户过期的session，过期的常为异常或者直接关闭浏览器，没有走正常注销的key
			clearOnlineSession(key);
		}

		redisCacheTemplate.hPut(userName, token, getCurrentTimeSeconds());
		int timeout = getTimeout(token);
		if (timeout > 0) {
			redisCacheTemplate.expire(token, timeout);
		}
	}

	private void clearOnlineSession(final String key) {
		redisCacheTemplate.hKeys(key).forEach((obj) -> {
			String hashKey = (String) obj;
			int timeout = getTimeout(hashKey);
			if (timeout > 0) {
				int oldTimeSecondsValue = Integer.valueOf((String) redisCacheTemplate.hGet(key, hashKey));
				int curTimeSecondsValue = (int) (System.currentTimeMillis()/1000);
				//如果 key-hashKey 对应的时间+过期时间 小于 当前时间，则剔除
				if(curTimeSecondsValue - (oldTimeSecondsValue+timeout) > 0) {
					redisCacheTemplate.hDel(key, hashKey);
				}
			}
		});
	}

	public boolean validateOnlineSession(final String key, final String hashKey) {
		int timeout = getTimeout(hashKey);
		if (timeout > 0) {
			String oldTimeSecondsValue = (String) redisCacheTemplate.hGet(key, hashKey);
			if (StringUtils.isEmpty(oldTimeSecondsValue)) {
				return false;
			} else {
				int curTimeSecondsValue = (int) (System.currentTimeMillis()/1000);
				if(Integer.valueOf(oldTimeSecondsValue)+timeout >= curTimeSecondsValue) {
					//刷新 key
					redisCacheTemplate.hPut(key, hashKey, getCurrentTimeSeconds());
					redisCacheTemplate.expire(key, timeout);
					return true;
				} else {
					redisCacheTemplate.hDel(key, hashKey);
					return false;
				}
			}
		} else {
			return redisCacheTemplate.hGet(key, hashKey) != null;
		}
	}
	
	// 注销用户时候需要调用
	public void delOnlineSession(final String key, final String hashKey){
		redisCacheTemplate.hDel(key, hashKey);
	}
    
	// 禁用或者删除用户时候调用
	public void deleteUserSession(final String key){
		redisCacheTemplate.delete(key);
	}
}
