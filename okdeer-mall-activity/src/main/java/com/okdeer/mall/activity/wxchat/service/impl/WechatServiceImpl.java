
package com.okdeer.mall.activity.wxchat.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.redis.IRedisTemplateWrapper;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wechat.dto.WechatConfigDto;
import com.okdeer.mall.activity.wxchat.bo.AddMediaResult;
import com.okdeer.mall.activity.wxchat.bo.CreateQrCodeResult;
import com.okdeer.mall.activity.wxchat.bo.JsApiTicketResult;
import com.okdeer.mall.activity.wxchat.bo.QueryMaterialResponse;
import com.okdeer.mall.activity.wxchat.bo.TokenInfo;
import com.okdeer.mall.activity.wxchat.bo.WechatBaseResult;
import com.okdeer.mall.activity.wxchat.bo.WechatUserInfo;
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
@Service
public class WechatServiceImpl implements WechatService {

	private static final Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);

	private static final String WECHAT_API_SERVER = "https://api.weixin.qq.com";

	private static final String WECHAT_API_GET_TOKEN_URL = WECHAT_API_SERVER + "/cgi-bin/token";

	private static final String WECHAT_API_CREATE_MENU_URL = WECHAT_API_SERVER + "/cgi-bin/menu/create";

	private static final String WECHAT_API_GET_MATERIAL_URL = WECHAT_API_SERVER + "/cgi-bin/material/batchget_material";

	private static final String ACESS_TOKEN_PARAM = "access_token";

	@Autowired
	private WechatConfig wechatConfig;

	private static final String CONFIG_KEY = "MALL-ACTIVITY-WECHAT-CONFIG";

	@Resource(name = "redisTemplateWrapper")
	private IRedisTemplateWrapper<String, WechatConfigDto> redisTemplateWrapper;

	private WechatConfigDto WECHAT_CONFIG = new WechatConfigDto();

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
		String response = HttpClient.post(WECHAT_API_CREATE_MENU_URL + getTokenUrl(), requestJson);
		WechatBaseResult baWechatBaseResult = JsonMapper.nonEmptyMapper().fromJson(response, WechatBaseResult.class);
		if (!baWechatBaseResult.isSuccess()) {
			throw new Exception(baWechatBaseResult.getErrMsg());
		}
	}

	private String getAcessToken() throws Exception {
		return getWechatConfig().getAccessToken();
	}

	private String getJsapiTicket(String accessToken) {
		try {
			String url = WECHAT_API_SERVER + "/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";
			String returnJson = HttpClient.get(url);
			JsApiTicketResult jsApiTicketResult = JsonMapper.nonEmptyMapper().fromJson(returnJson,
					JsApiTicketResult.class);
			return jsApiTicketResult.getTicket();
		} catch (Exception e) {
			logger.error("获取微信jsticket出错", e);
		}
		return null;
	}

	@Override
	public QueryMaterialResponse findMaterialList(String type, int pageNum, int pageSize) throws Exception {
		Map<String, Object> requestMap = Maps.newHashMap();
		requestMap.put("type", type);
		requestMap.put("offset", (pageNum - 1) * pageSize);
		requestMap.put("count", pageSize);
		String response = HttpClient.post(WECHAT_API_GET_MATERIAL_URL + getTokenUrl(),
				JsonMapper.nonEmptyMapper().toJson(requestMap));
		logger.debug("微信返回素材应答：{}：", response);
		return JsonMapper.nonEmptyMapper().fromJson(response, QueryMaterialResponse.class);
	}

	private String getTokenUrl() throws Exception {
		return "?" + ACESS_TOKEN_PARAM + "=" + getAcessToken();
	}

	private boolean isExpirToken(WechatConfigDto wechatConfigDto) {
		return StringUtils.isEmpty(wechatConfigDto.getAccessToken())
				|| System.currentTimeMillis() >= wechatConfigDto.getExpireTime().getTime();
	}

	@Override
	public WechatUserInfo getUserInfo(String fromUserName) throws Exception {
		String url = WECHAT_API_SERVER + "/cgi-bin/user/info" + getTokenUrl() + "&openid=" + fromUserName
				+ "&lang=zh_CN";
		logger.debug("获取token，请求url:{}", url);
		String resp = HttpClient.get(url);
		logger.debug("微信返回数据:{}", resp);
		if (resp == null) {
			throw new Exception("获取微信用户信息出错");
		}
		return JsonMapper.nonDefaultMapper().fromJson(resp, WechatUserInfo.class);
	}

	@Override
	public AddMediaResult addMedia(byte[] inputStream, String type, String fileName) throws Exception {
		String url = WECHAT_API_SERVER + "/cgi-bin/media/upload" + getTokenUrl() + "&type=" + type;
		String resp = HttpClient.postMultipart(url, inputStream, fileName);
		logger.info("上传素材返回信息：{}", resp);
		if (resp == null) {
			throw new Exception("上传素材信息出错");
		}
		return JsonMapper.nonDefaultMapper().fromJson(resp, AddMediaResult.class);
	}

	@Override
	public boolean send(String msginfo) {
		try {
			String url = WECHAT_API_SERVER + "/cgi-bin/message/custom/send" + getTokenUrl();
			String response = HttpClient.post(url, msginfo);
			logger.info("发送客服消息給用户，微信返回结果：{}", response);
			if (response == null) {
				logger.error("客服消息发送失败");
				return false;
			}
			WechatBaseResult result = JsonMapper.nonDefaultMapper().fromJson(response, WechatBaseResult.class);
			if (result.isSuccess()) {
				return true;
			}
			logger.warn("客服消息发送失败，微信返回错误信息{}", result.getErrMsg());
		} catch (ClientProtocolException e) {
			logger.error("协议错误", e);
		} catch (IOException e) {
			logger.error("网络出现异常", e);
		} catch (Exception e) {
			logger.error("获取token信息出错", e);
		}
		return false;
	}

	@Override
	public CreateQrCodeResult createQrCode(String sceneStr, int expireSeconds) throws Exception {
		Map<String, Object> sceneMap = Maps.newHashMap();
		sceneMap.put("scene_str", sceneStr);
		Map<String, Object> actionInfoMap = Maps.newHashMap();
		actionInfoMap.put("scene", sceneMap);
		Map<String, Object> requestMap = Maps.newHashMap();
		requestMap.put("action_name", "QR_LIMIT_STR_SCENE");
		requestMap.put("action_info", actionInfoMap);
		String url = WECHAT_API_SERVER + "/cgi-bin/qrcode/create" + getTokenUrl();
		String postData = JsonMapper.nonEmptyMapper().toJson(requestMap);
		String resp = HttpClient.post(url, postData);
		logger.debug("生成带参数的二维码返回数据:{}", resp);
		if (resp == null) {
			throw new Exception("生成带参数的二维码出错");
		}
		return JsonMapper.nonDefaultMapper().fromJson(resp, CreateQrCodeResult.class);
	}

	@Override
	public WechatConfigDto getWechatConfig() throws Exception {
		if (!isExpirToken(WECHAT_CONFIG)) {
			return WECHAT_CONFIG;
		}
		WechatConfigDto wechatConfigDto = redisTemplateWrapper.get(CONFIG_KEY);
		if (wechatConfigDto != null && !isExpirToken(wechatConfigDto)) {
			BeanMapper.copy(wechatConfigDto, WECHAT_CONFIG);
			return wechatConfigDto;
		}
		TokenInfo tokenInfo = getTokenInfo();
		if (!tokenInfo.isSuccess()) {
			throw new Exception("获取token出错," + tokenInfo.getErrMsg());
		}
		wechatConfigDto = new WechatConfigDto();
		wechatConfigDto.setAccessToken(tokenInfo.getAccessToken());
		long expireTime = System.currentTimeMillis() + (tokenInfo.getExpiresIn() - 60 * 30) * 1000;
		wechatConfigDto.setExpireTime(new Date(expireTime));
		String jsApiTicket = getJsapiTicket(tokenInfo.getAccessToken());
		wechatConfigDto.setJsApiTicket(jsApiTicket);
		wechatConfigDto.setAppid(wechatConfig.getAppId());
		BeanMapper.copy(wechatConfigDto, WECHAT_CONFIG);
		redisTemplateWrapper.set(CONFIG_KEY, wechatConfigDto, tokenInfo.getExpiresIn() - 60 * 30);
		return wechatConfigDto;
	}


}
