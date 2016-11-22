package com.okdeer.mall.order.pay.callback;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.system.utils.ConvertUtil;

@Service("serviceOrderPayHandler")
public class ServiceOrderPayHandler extends AbstractPayResultHandler{
	
	/**
	 * 商品信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuService;

	@Override
	protected void processOrderItem(TradeOrder tradeOrder) throws Exception{
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
	
	@Override
	public void sendTimerMessage(TradeOrder tradeOrder) throws Exception {
		// 预约服务时间
		Date serviceTime = DateUtils.parseDate(tradeOrder.getPickUpTime().substring(0,16), "yyyy-MM-dd HH:mm");
		// 预约服务时间过后2小时未接单的自动取消
		tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_accept_server_timeout, tradeOrder.getId(),
				(DateUtils.addHours(serviceTime, 2).getTime() - System.currentTimeMillis()) / 1000);
	}

}
