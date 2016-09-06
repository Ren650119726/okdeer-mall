/**   
* @Title: TradeOrderFlowService.java 
* @Package com.okdeer.mall.trade.order.service 
* @Description: TODO(用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2016年4月14日 上午9:55:52 
* @version V1.0   
*/
package com.okdeer.mall.order.service;

import net.sf.json.JSONObject;

/** 
* @ClassName: TradeOrderFlowService 
* @Description: 订单交易流程 </p>
* @author yangq
* @date 2016年4月14日 上午9:55:52 
*  
*/
public interface TradeOrderFlowService {

	/**
	 * 用户版App 结算
	 * 
	 * @author yangq
	 * @param requestStr
	 * @return
	 * @throws Exception
	 */
	public JSONObject selectValidateStoreSkuStock(String requestStr) throws Exception;

	/**
	 * 用户版App 确认订单
	 * 
	 * @author yangq
	 * @param requestStr
	 * @return
	 * @throws Exception
	 */
	public JSONObject addTradeOrder(String requestStr) throws Exception;

	/**
	 * 用户版App团购订单 结算
	 * 
	 * @author yangq
	 * @param requestStr
	 * @return
	 * @throws Exception
	 */
	public JSONObject selectValidateGroupTradeOrder(String requestStr) throws Exception;

	/**
	 * 用户版App团购 确认订单
	 * 
	 * @author yangq
	 * @param requestStr
	 * @return
	 * @throws Exception
	 */
	public JSONObject addGroupTradeOrder(String requestStr) throws Exception;

	/**
	 * pos 结算功能
	 * 
	 * @author yangq
	 * @param jsonData
	 * @return
	 */
	public JSONObject validateStock(JSONObject jsonData) throws Exception;

	/**
	 * pos 确认结算功能
	 * 
	 * @author yangq
	 * @param requestStr
	 * @return
	 */
	public JSONObject confirmSettleMent(String requestStr) throws Exception;

}
