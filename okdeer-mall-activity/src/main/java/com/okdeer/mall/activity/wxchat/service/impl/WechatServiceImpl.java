
package com.okdeer.mall.activity.wxchat.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.redis.IRedisTemplateWrapper;
import com.okdeer.mall.activity.wechat.dto.WechatConfigDto;
import com.okdeer.mall.activity.wxchat.bo.TokenInfo;
import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.service.WechatService;
import com.okdeer.mall.common.utils.HttpClientUtil;

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

	@Autowired
	private WechatConfig wechatConfig;
	
	private static final String CONFIG_KEY = "MALL-ACTIVITY-WECHAT-CONFIG";

	@Resource(name = "redisTemplateWrapper")
	private IRedisTemplateWrapper<String, WechatConfigDto> redisTemplateWrapper;
	
	
	@Override
	public TokenInfo getTokenInfo() throws Exception {
		String url = WECHAT_API_GET_TOKEN_URL + "?grant_type=client_credential&appid=" + wechatConfig.getAppId()
				+ "&secret=" + wechatConfig.getAppSecret();
		logger.debug("获取token，请求url:{}", url);
		String resp = HttpClientUtil.get(url);
		logger.debug("微信返回数据:{}", resp);
		if (resp == null) {
			throw new Exception("获取微信token出错");
		}
		return JsonMapper.nonDefaultMapper().fromJson(resp, TokenInfo.class);
	}

}
