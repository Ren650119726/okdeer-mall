package com.okdeer.mall.order.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;

public class RequestHandlerChain<Q,R> {
	
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
			handler.process(req, resp);
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
