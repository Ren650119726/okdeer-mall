
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.mall.activity.wechat.dto.WechatConfigDto;
import com.okdeer.mall.activity.wxchat.bo.AddMediaResult;
import com.okdeer.mall.activity.wxchat.bo.CreateQrCodeResult;
import com.okdeer.mall.activity.wxchat.bo.QueryMaterialResponse;
import com.okdeer.mall.activity.wxchat.bo.TokenInfo;
import com.okdeer.mall.activity.wxchat.bo.WechatUserInfo;

/**
 * ClassName: WechatService 
 * @Description: 微信service
 * @author zengjizu
 * @date 2017年7月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface WechatService {

	TokenInfo getTokenInfo() throws Exception;

	void createMenu(String requestJson) throws Exception;

	QueryMaterialResponse findMaterialList(String type, int pageNum, int pageSize) throws Exception;

	WechatUserInfo getUserInfo(String openid) throws Exception;

	AddMediaResult addMedia(byte[] inputStream, String type, String fileName) throws Exception;

	boolean send(String msginfo);

	CreateQrCodeResult createQrCode(String sceneStr, int expireSeconds) throws Exception;
	
	/**
	 * @Description: 获取微信配置
	 * @return
	 * @author zengjizu
	 * @date 2017年8月9日
	 */
	WechatConfigDto getWechatConfig() throws Exception;
}
