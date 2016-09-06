package com.okdeer.mall.system.factory;

/**
 * ClassName: StockAjustMsgBuilder 
 * @Description: 库存调整消息构造者
 * @author maojj
 * @date 2016年7月26日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-26			maojj			库存调整消息构造者
 */

public class StockAjustMsgBuilder extends AbstractStockMsgBuilder {

	@Override
	public String getDetailKey() {
		return "detailList";
	}

	@Override
	public String getNumKey() {
		return "adjustNum";
	}

	@Override
	public String getPriceKey() {
		return "adjustPrice";
	}


}
