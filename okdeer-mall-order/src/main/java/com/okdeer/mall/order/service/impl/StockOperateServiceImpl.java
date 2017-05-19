
package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreSkuAssembleDto;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.stock.dto.StockUpdateDto;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.GoodsStoreSkuStockApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.jxc.stock.service.StockUpdateServiceApi;
import com.okdeer.jxc.stock.vo.StockUpdateVo;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.order.builder.JxcStockUpdateBuilder;
import com.okdeer.mall.order.builder.MallStockUpdateBuilder;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.service.StockOperateService;

/**
 * ClassName: StockOperateServiceImpl 
 * @Description: 库存操作service实现类
 * @author zengjizu
 * @date 2016年11月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class StockOperateServiceImpl implements StockOperateService {

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;

	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockUpdateServiceApi stockUpdateServiceApi;

	@Resource
	private JxcStockUpdateBuilder jxcStockUpdateBuilder;
	
	@Resource
	private MallStockUpdateBuilder mallStockUpdateBuilder;

	
	@Autowired
	private TradeOrderItemMapper tradeOrderItemMapper;

	/**
	 * @Description: 根据订单回收库存
	 * @param tradeOrder
	 * @param rpcIdList
	 * @return
	 * @author zengjizu
	 * @date 2016年11月11日
	 */
	@Override
	public void recycleStockByOrder(TradeOrder tradeOrder, List<String> rpcIdList) throws Exception {
		StockUpdateDto mallStockUpdate = mallStockUpdateBuilder.build(tradeOrder);
		rpcIdList.add(mallStockUpdate.getRpcId());
		goodsStoreSkuStockApi.updateStock(mallStockUpdate);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void recycleStockByRefund(TradeOrder tradeOrder, TradeOrderRefunds orderRefunds,
			List<String> rpcIdList) throws Exception {
		List<String> orderItemIdList = extraOrderItemIdList(orderRefunds.getTradeOrderRefundsItem());
		List<TradeOrderItem> orderItemList = tradeOrderItemMapper.findOrderItemByIdList(orderItemIdList);
		
		StockUpdateDto mallStockUpdate = mallStockUpdateBuilder.build(orderRefunds, tradeOrder, orderItemList);
		StockUpdateVo jxcStockUpdate = jxcStockUpdateBuilder.build(orderRefunds, orderItemList);
		rpcIdList.add(mallStockUpdate.getRpcId());
		if(mallStockUpdate != null){
			goodsStoreSkuStockApi.updateStock(mallStockUpdate);
		}
		if(jxcStockUpdate != null){
			stockUpdateServiceApi.stockUpdateForMessage(jxcStockUpdate);
		}
	}
	
	public List<String> extraOrderItemIdList(List<TradeOrderRefundsItem> refundsItemList){
		List<String> orderItemIdList = new ArrayList<String>();
		for(TradeOrderRefundsItem refundsItem : refundsItemList){
			orderItemIdList.add(refundsItem.getOrderItemId());
		}
		return orderItemIdList;
	}

}
