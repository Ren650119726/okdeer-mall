
package com.okdeer.mall.activity.wxchat.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.redis.IRedisTemplateWrapper;
import com.okdeer.mall.activity.wechat.dto.WechatConfigDto;
import com.okdeer.mall.activity.wxchat.bo.TokenInfo;
import com.okdeer.mall.activity.wxchat.bo.WechatBaseResult;
import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.service.WechatService;
import com.okdeer.mall.activity.wxchat.util.HttpClient;

/**
 * ClassName: WechatServiceImpl 
 * @Description: 微信service实现
 * @author zengjizu
 * @date 2017年7月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class WechatServiceImpl implements WechatService {

	private static final Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);

	private static final String WECHAT_API_SERVER = "https://api.weixin.qq.com";

	private static final String WECHAT_API_GET_TOKEN_URL = WECHAT_API_SERVER + "/cgi-bin/token";

	private static final String WECHAT_API_CREATE_MENU_URL = WECHAT_API_SERVER + "/cgi-bin/menu/create";

	@Autowired
	private WechatConfig wechatConfig;

	private static final String CONFIG_KEY = "MALL-ACTIVITY-WECHAT-CONFIG";

	@Resource(name = "redisTemplateWrapper")
	private IRedisTemplateWrapper<String, WechatConfigDto> redisTemplateWrapper;

	private static WechatConfigDto WECHAT_CONFIG = new WechatConfigDto();

	@Override
	public TokenInfo getTokenInfo() throws Exception {
		String url = WECHAT_API_GET_TOKEN_URL + "?grant_type=client_credential&appid=" + wechatConfig.getAppId()
				+ "&secret=" + wechatConfig.getAppSecret();
		logger.debug("获取token，请求url:{}", url);
		String resp = HttpClient.get(url);
		logger.debug("微信返回数据:{}", resp);
		if (resp == null) {
			throw new Exception("获取微信token出错");
		}
		return JsonMapper.nonDefaultMapper().fromJson(resp, TokenInfo.class);
	}

	@Override
	public void createMenu(String requestJson) throws Exception {
		String response = HttpClient.post(WECHAT_API_CREATE_MENU_URL + getAcessToken(), requestJson);
		WechatBaseResult baWechatBaseResult = JsonMapper.nonEmptyMapper().fromJson(response, WechatBaseResult.class);
		if (!baWechatBaseResult.isSuccess()) {
			throw new Exception(baWechatBaseResult.getErrMsg());
		}
	}

	private String getAcessToken() throws Exception {
		if (!isExpirToken(WECHAT_CONFIG)) {
			return WECHAT_CONFIG.getAccessToken();
		}
		WechatConfigDto wechatConfigDto = redisTemplateWrapper.get(CONFIG_KEY);
		if (wechatConfigDto != null && !isExpirToken(wechatConfigDto)) {
			BeanMapper.copy(wechatConfigDto, WECHAT_CONFIG);
			return wechatConfigDto.getAccessToken();
		}
		TokenInfo tokenInfo = getTokenInfo();
		if (!tokenInfo.isSuccess()) {
			throw new Exception("获取token出错," + tokenInfo.getErrMsg());
		}
		wechatConfigDto = new WechatConfigDto();
		BeanMapper.copy(wechatConfigDto, WECHAT_CONFIG);
		redisTemplateWrapper.set(CONFIG_KEY, wechatConfigDto, tokenInfo.getExpiresIn() - 60 * 30);
		return tokenInfo.getAccessToken();
	}

	private boolean isExpirToken(WechatConfigDto wechatConfigDto) {
		return StringUtils.isEmpty(wechatConfigDto.getAccessToken())
				|| System.currentTimeMillis() >= wechatConfigDto.getExpireTime().getTime();
	}

}
