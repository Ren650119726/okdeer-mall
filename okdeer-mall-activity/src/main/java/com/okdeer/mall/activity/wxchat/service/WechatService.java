
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.mall.activity.wxchat.bo.QueryMaterialResponse;
import com.okdeer.mall.activity.wxchat.bo.TokenInfo;

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

	QueryMaterialResponse findMaterialList(String type, int pageNum, int pageSize) throws  Exception ;

}
