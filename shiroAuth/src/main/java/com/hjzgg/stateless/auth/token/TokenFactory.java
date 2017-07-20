package com.hjzgg.stateless.auth.token;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenFactory {

	private Map<String, ITokenProcessor> processors = new HashMap<String, ITokenProcessor>();

	public void setProcessors(List<ITokenProcessor> processors) {
		for (ITokenProcessor processor : processors) {
			this.processors.put(processor.getId(), processor);
		}
	}

	public ITokenProcessor getTokenProcessor(String token) {
		TokenInfo ti = getTokenInfo(token);
		return getTokenProcessorById(ti.getProcessor());
	}

	public ITokenProcessor getTokenProcessorById(String id) {
		return processors.get(id);
	}

	public static TokenInfo getTokenInfo(String token) {
		String ntoken = StringUtils.newStringUtf8(Base64.decodeBase64(StringUtils.getBytesUtf8(token)));
		String[] tokenInfo = ntoken.split(",");
		TokenInfo ti = new TokenInfo();
		ti.setProcessor(tokenInfo[0]);
		ti.setExpr(tokenInfo[1]);
		ti.setToken(tokenInfo[2]);
		return ti;
	}
}
