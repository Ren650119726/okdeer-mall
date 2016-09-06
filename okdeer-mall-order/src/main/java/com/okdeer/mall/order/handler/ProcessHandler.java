package com.okdeer.mall.order.handler;

import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderRespDto;

/**
 * ClassName: ProcessHandler 
 * @Description: 定义一个请求处理者，用于解耦复杂的处理流程
 * @author maojj
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		 重构V4.1			2016-07-14			maojj			 定义一个请求处理者，用于解耦复杂的处理流程
 */
public interface ProcessHandler {

	/**
	 * @Description: 
	 * @param reqDto 请求参数传输对象
	 * @param respDto 响应信息传输对象
	 * @throws Exception 抛出异常  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	void process(TradeOrderReqDto reqDto,TradeOrderRespDto respDto) throws Exception;
}
