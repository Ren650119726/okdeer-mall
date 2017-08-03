
package com.okdeer.mall.activity.wxchat.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.service.WechatMsgHandlerService;
import com.okdeer.mall.activity.wxchat.service.WechatMsgProcessService;
import com.okdeer.mall.activity.wxchat.util.WxchatUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;

@Service
public class WechatMsgProcessServiceImpl implements WechatMsgProcessService, WechatMsgHandlerService {

	private static final Logger logger = LoggerFactory.getLogger(Logger.class);

	private static final Map<String, WechatMsgHandler> msgHandlerMap = Maps.newHashMap();

	@Override
	public String process(String requestXml) throws MallApiException {
		String msgType = getMsgType(requestXml);
		if (msgType == null) {
			throw new MallApiException("解析msgType出错");
		}
		WechatMsgHandler wechatMsgHandler = msgHandlerMap.get(msgType);
		if (wechatMsgHandler == null) {
			logger.warn("没有找到相应的消息处理类：msgType={}", msgType);
			return null;
		}
		XStream xStream = new XStream(new Dom4JDriver());
		xStream.autodetectAnnotations(true);
		xStream.alias("xml", wechatMsgHandler.getRequestClass());
		Object requestObj = xStream.fromXML(requestXml);
		logger.debug("{}开始处理请求....", wechatMsgHandler.getClass().getName());
		Object response = wechatMsgHandler.process(requestObj);
		return xStream.toXML(response);
	}

	@Override
	public void addHandler(WechatMsgHandler wechatMsgHandler) throws MallApiException {
		if (msgHandlerMap.get(wechatMsgHandler.getMsgType()) != null) {
			throw new MallApiException("微信消息处理类重复,消息类型：" + wechatMsgHandler.getMsgType() + "，已经存在的类为："
					+ msgHandlerMap.get(wechatMsgHandler.getMsgType()).getClass());
		}
		msgHandlerMap.put(wechatMsgHandler.getMsgType(), wechatMsgHandler);
	}

	/**
	 *  获取签名
	 *  
	 * @param body
	 * @return
	 */
	private String getMsgType(String body) {

		String msgTypeNodeName = "<" + WxchatUtils.MSGTYPE + ">";
		String msgTypeEndNodeName = "</" + WxchatUtils.MSGTYPE + ">";

		int indexOfSignNode = body.indexOf(msgTypeNodeName);
		int indexOfSignEndNode = body.indexOf(msgTypeEndNodeName);

		if (indexOfSignNode < 0 || indexOfSignEndNode < 0) {
			return null;
		}
		String msgType = body.substring(indexOfSignNode + msgTypeNodeName.length(), indexOfSignEndNode);
		if (msgType.indexOf("CDATA") != -1) {
			msgType = msgType.substring("<![CDATA[".length() + 1, msgType.length() - "]]>".length());
			return msgType;
		}
		return msgType;
	}

}
