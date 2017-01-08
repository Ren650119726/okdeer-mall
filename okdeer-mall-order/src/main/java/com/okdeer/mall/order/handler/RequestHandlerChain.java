package com.okdeer.mall.order.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.okdeer.common.consts.LogConstants;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;

public class RequestHandlerChain<Q,R> {
	
	private static final Logger logger = LoggerFactory.getLogger(ProcessHandlerChain.class);
	/**
	 * 请求处理链
	 */
	private List<RequestHandler<Q,R>> handlerChain = new ArrayList<RequestHandler<Q,R>>();

	/**
	 * @Description: 处理app订单处理
	 * @param reqDto 封装的app请求参数
	 * @param respDto 封装的app响应信息
	 * @throws Exception 抛出异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	public void process(Request<Q> req,Response<R> resp) throws Exception {
		if (CollectionUtils.isEmpty(handlerChain)) {
			return;
		}
		for (RequestHandler<Q,R> handler : handlerChain) {
			long beginTime = System.currentTimeMillis();
			handler.process(req, resp);
			logger.debug(LogConstants.ORDER_FLOWER_CONSUME_TIME, req.getOrderOptType(),handler.getClass().getSimpleName(),System.currentTimeMillis()-beginTime);
			if (!resp.isSuccess()) {
				return;
			}
		}
	}

	public void addHandlerChain(RequestHandler<Q,R> handler){
		this.handlerChain.add(handler);
	}
	
	public void setHandlerChain(List<RequestHandler<Q,R>> handlerChain) {
		this.handlerChain = handlerChain;
	}

	
	public List<RequestHandler<Q, R>> getHandlerChain() {
		return handlerChain;
	}

}
