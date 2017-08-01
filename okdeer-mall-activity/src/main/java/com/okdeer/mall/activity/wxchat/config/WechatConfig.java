
package com.okdeer.mall.activity.wxchat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WechatConfig {

	/**
	 * 应用id
	 */
	@Value("${wechat.appId}")
	private String appId;

	/**
	 * 应用密钥
	 */
	@Value("${wechat.appSecret}")
	private String appSecret;

	/**
	 * 用于认证微信服务器用的token
	 */
	@Value("${wechat.token}")
	private String token;
	
	@Value("${wechat.api.url}")

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
