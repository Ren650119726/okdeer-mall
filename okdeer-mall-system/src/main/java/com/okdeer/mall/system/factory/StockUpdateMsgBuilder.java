package com.okdeer.mall.system.factory;

import net.sf.json.JSONObject;


/**
 * ClassName: StockUpdateMsgBuilder 
 * @Description: 库存更新消息构造者
 * @author maojj
 * @date 2016年7月26日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-26			maojj			库存更新消息构造者
 */

public class StockUpdateMsgBuilder extends AbstractStockMsgBuilder {

	@Override
	public String getDetailKey() {
		return "details";
	}

	@Override
	public void putType(JSONObject msgJson,String operateType) {
		msgJson.put("type", operateType);
	}
	
	@Override
	public void putOrderId(JSONObject msgJson,String orderId) {
		msgJson.put("orderId", clean(orderId));
	}

	@Override
	public String getNumKey() {
		return "num";
	}

	@Override
	public String getPriceKey() {
		return "price";
	}

}
