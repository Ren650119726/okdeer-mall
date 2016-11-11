package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.mall.order.entity.TradeOrder;

/**
 * ClassName: StockOperateService 
 * @Description: 库存操作接口
 * @author zengjizu
 * @date 2016年11月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface StockOperateService {
	
	/**
	 * @Description: 根据订单回收库存
	 * @param tradeOrder
	 * @param rpcIdList
	 * @return
	 * @author zengjizu
	 * @date 2016年11月11日
	 */
	List<StockAdjustVo> recycleStockByOrder(TradeOrder tradeOrder,List<String> rpcIdList) throws Exception ;
	
}
