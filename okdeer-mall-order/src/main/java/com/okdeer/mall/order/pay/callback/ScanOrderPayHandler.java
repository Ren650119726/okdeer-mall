
package com.okdeer.mall.order.pay.callback;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.system.utils.ConvertUtil;

/**
 * ClassName: ScanOrderPayHandler 
 * @Description: 扫码够订单支付成功后处理
 * @author zengjizu
 * @date 2017年3月24日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    V2.2.0              2017-3-24          zengjz           扫码够订单支付成功后处理
 */
@Service("scanOrderPayHandler")
public class ScanOrderPayHandler extends AbstractPayResultHandler {
	
	/**
	 * 商品信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuService;
	
	@Override
	protected void setOrderStatus(TradeOrder tradeOrder) {
		tradeOrder.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
	}
	
	@Override
	public void postProcessOrder(TradeOrder tradeOrder) throws Exception {
		
	}

	@Override
	protected void sendNotifyMessage(TradeOrder tradeOrder) throws Exception {
		
	}
	
	@Override
	public void sendTimerMessage(TradeOrder tradeOrder) throws Exception {
		
	}
	
	@Override
	public void preProcessOrder(TradeOrder tradeOrder) throws Exception {
		
	}
	
	/**
	 * 扫描购支付完成及为订单完成
	 * 更新扫描购订单商品的销量
	 */
	@Override
	protected void processOrderItem(TradeOrder tradeOrder) throws Exception {
		List<TradeOrderItem> orderItemList = tradeOrderItemMapper.selectOrderItemListById(tradeOrder.getId());
		for (TradeOrderItem orderItem : orderItemList) {
			// 线上支付的，支付完成，销量增加
			GoodsStoreSku goodsStoreSku = this.goodsStoreSkuService.getById(orderItem.getStoreSkuId());
			if (goodsStoreSku != null) {
				goodsStoreSku.setSaleNum(ConvertUtil.format(goodsStoreSku.getSaleNum()) + orderItem.getQuantity());
				goodsStoreSkuService.updateByPrimaryKeySelective(goodsStoreSku);
			}
		}
	}
}
