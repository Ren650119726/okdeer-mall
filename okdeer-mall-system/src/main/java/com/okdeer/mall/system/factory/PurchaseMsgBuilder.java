package com.okdeer.mall.system.factory;

/**
 * ClassName: PurchaseMsgBuilder 
 * @Description: 采购消息构造者
 * @author maojj
 * @date 2016年7月26日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-26			maojj				采购消息构造者
 */

public class PurchaseMsgBuilder extends AbstractStockMsgBuilder {

	@Override
	public String getDetailKey() {
		return "detailList";
	}

	public String getNumKey() {
		return "purchaseNum";
	}

	@Override
	public String getPriceKey() {
		return "purchasePrice";
	}

}
