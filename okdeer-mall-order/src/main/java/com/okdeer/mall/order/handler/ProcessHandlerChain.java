package com.okdeer.mall.order.handler;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderRespDto;

/**
 * ClassName: ProcessHandlerChain 
 * @Description: 请求处理链对象
 * @author maojj
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		重构V4.1			2016-07-14			maojj			 请求处理链对象
 */
public class ProcessHandlerChain {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProcessHandlerChain.class);

	/**
	 * 请求处理链
	 */
	private List<ProcessHandler> handlerChain;

	/**
	 * @Description: 处理app订单处理
	 * @param reqDto 封装的app请求参数
	 * @param respDto 封装的app响应信息
	 * @throws Exception 抛出异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		if (CollectionUtils.isEmpty(handlerChain)) {
			return;
		}
		for (ProcessHandler handler : handlerChain) {
			long beginTime = System.currentTimeMillis();
			handler.process(reqDto, respDto);
			LOG.info("订单操作类型为：{}，执行流程：{}，耗时：{}" , reqDto.getOrderOptType().getDescription(),handler.getClass().getSimpleName(),System.currentTimeMillis()-beginTime);
			if (!respDto.isFlag()) {
				return;
			}
		}
	}

	/**
	 * @Description: 根据请求处理列表获取请求处理链实例对象
	 * @param handlers 请求处理者
	 * @return ProcessHandlerChain  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	public static ProcessHandlerChain getInstance(ProcessHandler... handlers) {
		ProcessHandlerChain chain = new ProcessHandlerChain();
		chain.setHandlerChain(Arrays.asList(handlers));
		return chain;
	}

	/**
	 * @Description: 根据请求处理列表获取请求处理链实例对象
	 * @param handlerChain 请求处理链
	 * @return ProcessHandlerChain  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	public static ProcessHandlerChain getInstance(List<ProcessHandler> handlerChain) {
		ProcessHandlerChain chain = new ProcessHandlerChain();
		chain.setHandlerChain(handlerChain);
		return chain;
	}

	public List<ProcessHandler> getHandlerChain() {
		return handlerChain;
	}

	public void setHandlerChain(List<ProcessHandler> handlerChain) {
		this.handlerChain = handlerChain;
	}
}
