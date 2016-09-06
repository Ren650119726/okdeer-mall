package com.okdeer.mall.system.factory;

import com.okdeer.archive.stock.vo.StockAdjustVo;

/**
 * ClassName: MessageFactory 
 * @Description: 消息工厂
 * @author maojj
 * @date 2016年7月26日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-26			maojj				消息工厂
 */

public class MessageFactory {

	/**
	 * 库存调整
	 */
	public static final int STOCK_AJUST = 0;

	/**
	 * 采购
	 */
	public static final int PURCHASE = 1;

	/**
	 * 库存修改
	 */
	public static final int STOCK_UPDATE = 2;

	/**
	 * @Description: 重载构建消息
	 * @param stockAdjustVo 库存调整Vo
	 * @param type 0：库存调整，1：采购，2：库存修改
	 * @return 消息 
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public static String buildMessage(StockAdjustVo stockAdjustVo, int type) {
		return buildMessage(stockAdjustVo, type, null);
	}

	/**
	 * @Description: 重载构建消息
	 * @param stockAdjustVo 库存调整Vo
	 * @param type 0：库存调整，1：采购，2：库存修改
	 * @param operatorType erp操作类型码
	 * @return 消息 
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public static String buildMessage(StockAdjustVo stockAdjustVo, int type, String operatorType) {
		AbstractStockMsgBuilder builder = null;
		switch (type) {
			case STOCK_AJUST:
				builder = new StockAjustMsgBuilder();
				break;
			case PURCHASE:
				builder = new PurchaseMsgBuilder();
				break;
			case STOCK_UPDATE:
				builder = new StockUpdateMsgBuilder();
				break;
			default : break;
		}
		if (operatorType == null) {
			return builder.buildMsg(stockAdjustVo);
		} else {
			return builder.buildMsg(stockAdjustVo, operatorType);
		}

	}
}
