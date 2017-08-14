
package com.okdeer.mall.activity.wxchat.service.impl;

import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.google.common.collect.Maps;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.annotation.XStreamCDATA;
import com.okdeer.mall.activity.wxchat.bo.WechatMsgRequest;
import com.okdeer.mall.activity.wxchat.service.CustomerService;
import com.okdeer.mall.activity.wxchat.service.WechatMsgHandlerService;
import com.okdeer.mall.activity.wxchat.service.WechatMsgProcessService;
import com.okdeer.mall.activity.wxchat.util.WxchatUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

@Service
public class WechatMsgProcessServiceImpl
		implements WechatMsgProcessService, WechatMsgHandlerService, InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(Logger.class);

	private static final Map<String, WechatMsgHandler> msgHandlerMap = Maps.newHashMap();

	private ExecutorService cachedThreadPool;

	private static final String DEFAULT_RESPONSE = "success";

	@Autowired
	private CustomerService customerService;

	@Override
	public String process(String requestXml) throws MallApiException {
		WechatMsgRequest wechatMsgRequest = new WechatMsgRequest(requestXml);
		submitRequest(wechatMsgRequest);
		// 等待3秒钟
		boolean result;
		try {
			result = wechatMsgRequest.getCountDownLatch().await(3, TimeUnit.SECONDS);
			if (!result) {
				wechatMsgRequest.setTimeOut(true);
				return DEFAULT_RESPONSE;
			}

			if (wechatMsgRequest.getResult() == null) {
				return DEFAULT_RESPONSE;
			}
			XStream xStream = createXstream();
			xStream.autodetectAnnotations(true);
			return xStream.toXML(wechatMsgRequest.getResult());
		} catch (InterruptedException e) {
			logger.error("当前线程{}已经中断", Thread.currentThread().getName(), e);
			Thread.currentThread().interrupt();
		}
		return DEFAULT_RESPONSE;
	}

	/**
	 * @Description:提交请求給线程池执行
	 * @param wechatMsgRequest
	 * @author zengjizu
	 * @date 2017年8月5日
	 */
	private void submitRequest(WechatMsgRequest wechatMsgRequest) {
		cachedThreadPool.execute(() -> {
			try {
				Object result = doProcess(wechatMsgRequest);
				wechatMsgRequest.setResult(result);
				if (wechatMsgRequest.isTimeOut() && result != null) {
					// 已经处理超时了，并且返回信息不为null，此时将消息异步通过客服接口发送給用户
					customerService.sendMsg(result);
				}
			} catch (Exception e) {
				logger.error("处理微信请求出错", e);
			}
			wechatMsgRequest.getCountDownLatch().countDown();
		});
	}

	private Object doProcess(WechatMsgRequest wechatMsgRequest) throws MallApiException {

		String msgType = getMsgType(wechatMsgRequest.getRequestXml());
		if (msgType == null) {
			throw new MallApiException("解析msgType出错");
		}
		String event = getEvent(wechatMsgRequest.getRequestXml());
		if (event == null) {
			throw new MallApiException("解析event出错");
		}

		String key = (msgType + WxchatUtils.JOIN + event).toUpperCase();
		WechatMsgHandler wechatMsgHandler = msgHandlerMap.get(key);
		if (wechatMsgHandler == null) {
			logger.warn("没有找到相应的消息处理类：msgType={},event={}", msgType, event);
			throw new MallApiException("没有找到相应的消息处理类");
		}
		XStream xStream = createXstream();
		xStream.autodetectAnnotations(true);
		xStream.alias("xml", wechatMsgHandler.getRequestClass());
		Object requestObj = xStream.fromXML(wechatMsgRequest.getRequestXml());
		logger.debug("{}开始处理请求....", wechatMsgHandler.getClass().getName());
		Object response = wechatMsgHandler.process(requestObj);
		return response;
	}

	@Override
	public void addHandler(WechatMsgHandler wechatMsgHandler) throws MallApiException {
		String key = wechatMsgHandler.getMsgTypeEvent().toUpperCase();
		logger.info("添加处理类:{}", key);
		if (msgHandlerMap.get(key) != null) {
			throw new MallApiException("微信消息处理类重复,消息类型：" + key + "，已经存在的类为：" + msgHandlerMap.get(key).getClass());
		}
		msgHandlerMap.put(key, wechatMsgHandler);
		logger.info("{}", msgHandlerMap);
	}

	/**
	 *  获取签名
	 *  
	 * @param body
	 * @return
	 */
	private String getMsgType(String body) {
		return getElement(body, WxchatUtils.MSGTYPE);
	}

	private String getEvent(String body) {
		return getElement(body, WxchatUtils.EVENT);
	}

	private String getElement(String body, String elementName) {
		String msgTypeNodeName = "<" + elementName + ">";
		String msgTypeEndNodeName = "</" + elementName + ">";
		int indexOfSignNode = body.indexOf(msgTypeNodeName);
		int indexOfSignEndNode = body.indexOf(msgTypeEndNodeName);

		if (indexOfSignNode < 0 || indexOfSignEndNode < 0) {
			return null;
		}
		String content = body.substring(indexOfSignNode + msgTypeNodeName.length(), indexOfSignEndNode);
		if (content.indexOf("CDATA") != -1) {
			content = content.substring("<![CDATA[".length(), content.length() - "]]>".length());
			return content;
		}
		return content;
	}

	private XStream createXstream() {
		return new XStream(new XppDriver() {

			@Override
			public HierarchicalStreamWriter createWriter(Writer out) {
				return new PrettyPrintWriter(out) {

					boolean cdata = false;

					Class<?> targetClass = null;

					@Override
					public void startNode(String name, @SuppressWarnings("rawtypes") Class clazz) {
						super.startNode(name, clazz);
						// 业务处理，对于用XStreamCDATA标记的Field，需要加上CDATA标签
						if (!name.equals("xml")) {// 代表当前处理节点是class，用XstreamAlias把class的别名改成xml，下面的代码片段有提到
							cdata = needCDATA(targetClass, name);
						} else {
							targetClass = clazz;
						}
					}

					@Override
					protected void writeText(QuickWriter writer, String text) {
						if (cdata) {
							writer.write(cDATA(text));
						} else {
							writer.write(text);
						}
					}

				};
			}
		});
	}

	private String cDATA(String text) {
		return "<![CDATA[" + text + "]]>";
	}

	private boolean needCDATA(Class<?> targetClass, String fieldAlias) {
		boolean cdata = false;
		// first, scan self
		cdata = existsCDATA(targetClass, fieldAlias);
		if (cdata)
			return cdata;
		// if cdata is false, scan supperClass until java.lang.Object
		Class<?> superClass = targetClass.getSuperclass();
		while (!superClass.equals(Object.class)) {
			cdata = existsCDATA(superClass, fieldAlias);
			if (cdata) {
				return cdata;
			}
			superClass = superClass.getSuperclass();
		}
		return false;
	}

	private boolean existsCDATA(Class<?> clazz, String fieldAlias) {
		// scan fields
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			// 1. exists XStreamCDATA
			if (field.getAnnotation(XStreamCDATA.class) != null) {
				XStreamAlias xStreamAlias = field.getAnnotation(XStreamAlias.class);
				// 2. exists XStreamAlias
				if (null != xStreamAlias) {
					if (fieldAlias.equals(xStreamAlias.value()))// matched
						return true;
				} else {// not exists XStreamAlias
					if (fieldAlias.equals(field.getName()))
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public void destroy() throws Exception {
		cachedThreadPool.shutdown();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		cachedThreadPool = Executors
				.newCachedThreadPool(new ThreadFactoryImpl("WechatMsgProcessServiceExecutorThread_"));
	}

}
