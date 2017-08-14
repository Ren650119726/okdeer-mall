
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.common.exception.MallApiException;

public interface WechatMsgProcessService {

	/**
	 * @Description: 消息处理
	 * @param requestXml
	 * @return
	 * @author zengjizu
	 * @date 2017年8月3日
	 */
	String process(String requestXml) throws MallApiException;

}
